package me.cares.securityexam.security.authentication.token.bearer.access

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import me.cares.securityexam.security.authentication.token.Token
import me.cares.securityexam.security.authentication.token.TokenBasedAuthentication
import me.cares.securityexam.security.authentication.token.bearer.BearerToken
import me.cares.securityexam.security.authentication.token.bearer.refresh.RefreshToken
import org.springframework.security.core.Authentication
import java.time.Instant
import java.util.*

class AccessToken private constructor(
    value: String,
) : BearerToken(
    value = value,
    secretKey = SECRET_KEY,
    expirationTimeInSecond = EXPIRATION_TIME_IN_SECOND
) {

    fun makeAuthentication(): Authentication {
        return TokenBasedAuthentication.authenticated(
            id = UUID.fromString(this.subject),
            role = getClaim("role")!!.asList(String::class.java),
            token = this
        )
    }

    companion object {

        private const val SECRET_KEY = "secret_key_secret_key_secret_key_secret_key_secret_key_secret_key"
        private const val EXPIRATION_TIME_IN_SECOND = 30 * 60L
        private const val EXPIRATION_TIME_IN_MILLIS = EXPIRATION_TIME_IN_SECOND * 1000L

        fun generateNew(
            accountId: String,
            role: List<String>,
        ): Token {
            val tokenString = JWT
                .create()
                .withSubject(accountId)
                .withClaim("role", role)
                .withClaim("type", AccessToken::class.simpleName)
                .withExpiresAt(Instant.now().plusSeconds(EXPIRATION_TIME_IN_SECOND))
                .sign(Algorithm.HMAC512(SECRET_KEY))

            return AccessToken(tokenString)
        }

        fun fromString(token: String): AccessToken {
            return AccessToken(token)
        }

    }

}