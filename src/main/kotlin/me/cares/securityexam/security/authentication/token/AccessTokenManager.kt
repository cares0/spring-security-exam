package me.cares.securityexam.security.authentication.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.*
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.http.HttpServletRequest
import me.cares.securityexam.application.domain.AccountRole
import me.cares.securityexam.security.authentication.api.CustomUserDetails
import me.cares.securityexam.security.authentication.token.exception.ExpiredTokenException
import me.cares.securityexam.security.authentication.token.exception.InvalidTokenException
import me.cares.securityexam.security.authentication.token.exception.TokenTypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.util.Base64
import java.util.Base64.Decoder
import java.util.UUID
import java.util.regex.Pattern

@Component
class AccessTokenManager(

) : TokenResolver,
    TokenGenerator,
    TokenDecoder {

    private val authorizationPattern = Pattern.compile(
        "^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
        Pattern.CASE_INSENSITIVE
    )
    private val accessTokenHeaderName: String = HttpHeaders.AUTHORIZATION

    private val tokenType = Token.ACCESS.name
    private val secretKey = Token.ACCESS.secretKey
    private val expirationTimeInSecond = Token.ACCESS.expirationTimeInSecond
    private val secretAlgorithm = Algorithm.HMAC512(Base64.getDecoder().decode(secretKey))

    override fun resolve(request: HttpServletRequest): String? {
        val authorization = request.getHeader(accessTokenHeaderName) ?: return null
        if (!authorization.startsWith("bearer", true)) return null
        val matcher = authorizationPattern.matcher(authorization)
        return if (!matcher.matches()) throw TokenTypeMismatchException()
        else matcher.group("token")
    }

    override fun decodeToUserDetails(rawToken: String): CustomUserDetails {
        return try {
            val decodedAccessToken = decodeToken(rawToken)

            makeCustomUserDetails(decodedAccessToken, rawToken)
        } catch (e: JWTVerificationException) {
            handleJWTException(e)
        }
    }

    private fun makeCustomUserDetails(
        decodedAccessToken: DecodedJWT,
        rawToken: String
    ) = CustomUserDetails(
        id = UUID.fromString(decodedAccessToken.subject),
        email = decodedAccessToken.claims["email"]?.asString(),
        password = rawToken,
        role = AccountRole.valueOf(decodedAccessToken.claims["role"]!!.asString())
    )

    private fun decodeToken(rawToken: String): DecodedJWT = JWT
        .require(secretAlgorithm)
        .withClaim("type", tokenType)
        .withClaim("role") { claim, _ -> !claim.asString().isNullOrEmpty() }
        .build()
        .verify(rawToken)

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

    override fun generateFromUserDetails(customUserDetails: CustomUserDetails): String {
        val email = customUserDetails.email
        val subject = customUserDetails.username
        val expiredAt = Instant.now().plusSeconds(expirationTimeInSecond)
        val role = customUserDetails.authorities.first().authority

        return JWT
            .create()
            .withSubject(subject)
            .withExpiresAt(expiredAt)
            .withClaim("role", role)
            .withClaim("type", tokenType)
            .withClaim("email", email)
            .sign(secretAlgorithm)
    }

}