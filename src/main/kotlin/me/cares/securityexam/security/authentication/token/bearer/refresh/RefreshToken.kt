package me.cares.securityexam.security.authentication.token.bearer.refresh

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import me.cares.securityexam.security.authentication.token.Token
import me.cares.securityexam.security.authentication.token.bearer.BearerToken
import java.time.Instant

class RefreshToken(
    value: String,
) : BearerToken(
    value = value,
    secretKey = SECRET_KEY,
    expirationTimeInSecond = EXPIRATION_TIME_IN_SECOND
) {

    companion object {

        private const val SECRET_KEY = "secret_key_secret_key_secret_key_secret_key_secret_key_secret_key"
        private const val EXPIRATION_TIME_IN_SECOND = 7 * 24 * 60 * 60L
        private const val EXPIRATION_TIME_IN_MILLIS = EXPIRATION_TIME_IN_SECOND * 1000L

        fun generateNew(
            accountId: String,
        ): Token {
            val tokenString = JWT
                .create()
                .withSubject(accountId)
                .withClaim("type", RefreshToken::class.simpleName)
                .withExpiresAt(Instant.now().plusSeconds(EXPIRATION_TIME_IN_SECOND))
                .sign(Algorithm.HMAC512(SECRET_KEY))

            return RefreshToken(tokenString)
        }

        fun fromString(token: String): Token {
            return RefreshToken(token)
        }

    }
}