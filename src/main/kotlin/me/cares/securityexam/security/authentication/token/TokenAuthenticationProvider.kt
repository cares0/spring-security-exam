package me.cares.securityexam.security.authentication.token

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

class TokenAuthenticationProvider(
    private val accessTokenDecoder: TokenDecoder,
    private val refreshTokenDecoder: TokenDecoder,
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val tokenAuthentication = authentication as TokenBasedAuthentication
        val rawToken = tokenAuthentication.rawToken
        val tokenType = tokenAuthentication.type

        val userDetails = when (tokenType) {
            Token.ACCESS -> accessTokenDecoder.decodeToUserDetails(rawToken)
            Token.REFRESH -> refreshTokenDecoder.decodeToUserDetails(rawToken)
        }

        return TokenBasedAuthentication.successfulAuthentication(
            tokenType = tokenType,
            rawToken = rawToken,
            userDetails = userDetails
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication.isAssignableFrom(TokenBasedAuthentication::class.java)
    }

}