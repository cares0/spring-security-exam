package me.cares.securityexam.security.authentication.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.*
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import me.cares.securityexam.application.domain.RefreshToken
import me.cares.securityexam.persistence.EntityNotExistException
import me.cares.securityexam.persistence.RefreshTokenRepository
import me.cares.securityexam.persistence.findByIdOrThrow
import me.cares.securityexam.security.authentication.api.CustomUserDetails
import me.cares.securityexam.security.authentication.token.exception.ExpiredTokenException
import me.cares.securityexam.security.authentication.token.exception.InvalidTokenCookieException
import me.cares.securityexam.security.authentication.token.exception.InvalidTokenException
import me.cares.securityexam.security.authentication.token.exception.UnavailableTokenException
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Component
@Transactional
class RefreshTokenManager(
    private val refreshTokenRepository: RefreshTokenRepository,
) : TokenResolver, TokenGenerator, TokenDecoder {

    private val refreshTokenCookieName: String = Token.REFRESH.name

    private val tokenType = Token.REFRESH.name
    private val secretKey = Token.REFRESH.secretKey
    private val expirationTimeInSecond = Token.REFRESH.expirationTimeInSecond
    private val secretAlgorithm = Algorithm.HMAC512(Base64.getDecoder().decode(secretKey))

    override fun resolve(request: HttpServletRequest): String? {
        val refreshTokenCookie = findRefreshTokenCookie(request) ?: return null
        return refreshTokenCookie.value
    }

    private fun findRefreshTokenCookie(request: HttpServletRequest) =
        request.cookies.firstOrNull { it.name == refreshTokenCookieName }

    override fun decodeToUserDetails(rawToken: String): CustomUserDetails {
        return try {
            val decodedRefreshToken = decodeToken(rawToken)

            val accountRefreshToken = getAccountRefreshToken(decodedRefreshToken)

            if (accountRefreshToken.isSameToken(decodedRefreshToken.token))
                makeCustomUserDetails(accountRefreshToken)
            else throw UnavailableTokenException()
        } catch (e: JWTVerificationException) {
            handleJWTException(e)
        } catch (e: EntityNotExistException) {
            throw InvalidTokenException(e)
        }
    }

    private fun handleJWTException(e: JWTVerificationException): Nothing {
        when (e) {
            is MissingClaimException,
            is AlgorithmMismatchException,
            is SignatureVerificationException,
            is InvalidClaimException -> throw InvalidTokenException(e)
            is TokenExpiredException -> throw ExpiredTokenException(e)
            else -> throw IllegalStateException("처리하지 않은 예외입니다.", e)
        }
    }

    private fun makeCustomUserDetails(accountRefreshToken: RefreshToken): CustomUserDetails {
        val account = accountRefreshToken.account

        val customUserDetails = CustomUserDetails(
            id = account.id,
            email = account.email,
            password = account.password,
            role = account.role
        )
        return customUserDetails
    }

    private fun getAccountRefreshToken(decodedRefreshToken: DecodedJWT): RefreshToken {
        val accountId = UUID.fromString(decodedRefreshToken.subject)

        return refreshTokenRepository.findByIdOrThrow(accountId)
    }

    private fun decodeToken(rawToken: String): DecodedJWT = JWT
        .require(secretAlgorithm)
        .withClaim("type", tokenType)
        .build()
        .verify(rawToken)

    override fun generateFromUserDetails(customUserDetails: CustomUserDetails): String {
        val subject = customUserDetails.username
        val expiredAt = Instant.now().plusSeconds(expirationTimeInSecond)

        return JWT
            .create()
            .withSubject(subject)
            .withExpiresAt(expiredAt)
            .withClaim("type", tokenType)
            .sign(secretAlgorithm)
    }

}