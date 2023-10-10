package me.cares.securityexam.security.authentication.token.bearer

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.Claim
import me.cares.securityexam.security.authentication.token.ExpiredTokenException
import me.cares.securityexam.security.authentication.token.InvalidTokenException
import me.cares.securityexam.security.authentication.token.TokenException
import me.cares.securityexam.security.authentication.token.Token

abstract class BearerToken(
    final override val secretKey: String,
    final override val expirationTimeInSecond: Long,
    final override val value: String,
) : Token() {

    final override val subject: String
    final override val claims: Map<String, Claim>

    fun getClaim(claimName: String): Claim? {
        return claims[claimName]
    }

    init {
        try {
            val decodedJWT = JWT
                .require(Algorithm.HMAC512(secretKey))
                .acceptExpiresAt(expirationTimeInSecond)
                .withClaim("type", this.getType())
                .build()
                .verify(value)

            this.claims = decodedJWT.claims
            this.subject = decodedJWT.subject
        } catch (e: JWTVerificationException) {
            when (e) {
                is AlgorithmMismatchException,
                is SignatureVerificationException,
                is InvalidClaimException -> throw InvalidTokenException(e)
                is TokenExpiredException -> throw ExpiredTokenException(e)
                else -> throw IllegalStateException("처리하지 않은 예외입니다.", e)
            }
        }
    }

}