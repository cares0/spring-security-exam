package me.cares.securityexam.security.authentication.api

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import me.cares.securityexam.application.domain.RefreshToken
import me.cares.securityexam.persistence.AccountRepository
import me.cares.securityexam.persistence.RefreshTokenRepository
import me.cares.securityexam.persistence.findByIdOrThrow
import me.cares.securityexam.security.authentication.token.Token
import me.cares.securityexam.security.authentication.token.TokenGenerator
import me.cares.securityexam.web.ApiResponse
import org.springframework.boot.web.server.Cookie.SameSite
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.RequestMatcher

@Transactional
open class ApiAuthenticationSuccessHandler(
    private val apiLoginRequestMatcher: RequestMatcher,
    private val objectMapper: ObjectMapper,
    private val accessTokenGenerator: TokenGenerator,
    private val refreshTokenGenerator: TokenGenerator,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val accountRepository: AccountRepository,
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val userDetails = retrieveCustomUserDetails(authentication)

        val newAccessToken = generateNewAccessToken(userDetails)
        val newRefreshToken = generateNewRefreshToken(userDetails)

        setResponse(request, response, newAccessToken, newRefreshToken)
    }

    private fun setResponse(
        request: HttpServletRequest,
        response: HttpServletResponse,
        newAccessToken: String,
        newRefreshToken: String,
    ) {
        setResponseAttributes(request, response, newRefreshToken)

        writeResponse(response, newAccessToken)
    }

    private fun writeResponse(
        response: HttpServletResponse,
        newAccessToken: String,
    ) {
        val apiResponse = ApiResponse.ofSuccess(
            ApiAuthenticationTokenResponse(
                token = newAccessToken,
                type = Token.ACCESS.name
            )
        )

        objectMapper.writeValue(
            response.writer,
            apiResponse
        )
    }

    private fun setResponseAttributes(
        request: HttpServletRequest,
        response: HttpServletResponse,
        newRefreshToken: String
    ) {
        val isApiLoginRequest = apiLoginRequestMatcher.matches(request)

        if (isApiLoginRequest) {
            response.status = HttpStatus.OK.value()
        } else {
            response.status = HttpStatus.UNAUTHORIZED.value()
        }

        val refreshTokenCookie = makeRefreshTokenCookie(newRefreshToken)
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie)
    }

    private fun makeRefreshTokenCookie(newRefreshToken: String) = ResponseCookie
        .from(Token.REFRESH.name, newRefreshToken)
        .httpOnly(true)
        .sameSite(SameSite.STRICT.attributeValue())
        .build()
        .toString()

    private fun generateNewRefreshToken(userDetails: CustomUserDetails): String {
        val newRefreshToken = refreshTokenGenerator.generateFromUserDetails(userDetails)

        rotateToNewToken(userDetails, newRefreshToken)

        return newRefreshToken
    }

    private fun rotateToNewToken(
        userDetails: CustomUserDetails,
        newRefreshToken: String
    ) {
        val accountRefreshToken = refreshTokenRepository.findByIdOrNull(userDetails.id)

        if (accountRefreshToken == null) {
            val account = accountRepository.findByIdOrThrow(userDetails.id)
            val initialRefreshToken = RefreshToken(account, newRefreshToken)
            refreshTokenRepository.save(initialRefreshToken)
        } else {
            accountRefreshToken.rotateToken(newRefreshToken)
            refreshTokenRepository.save(accountRefreshToken)
        }

    }

    private fun generateNewAccessToken(userDetails: CustomUserDetails) =
        accessTokenGenerator.generateFromUserDetails(userDetails)

    private fun retrieveCustomUserDetails(authentication: Authentication): CustomUserDetails {
        val userDetails = authentication.principal

        if (userDetails !is CustomUserDetails)
            throw IllegalStateException("인증에서 사용하지 않는 UserDetails 타입입니다.")
        return userDetails
    }

}