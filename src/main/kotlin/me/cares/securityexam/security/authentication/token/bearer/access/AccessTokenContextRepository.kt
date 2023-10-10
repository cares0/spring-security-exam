package me.cares.securityexam.security.authentication.token.bearer.access

import me.cares.securityexam.security.authentication.token.Token
import me.cares.securityexam.security.authentication.token.TokenContextRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext

class AccessTokenContextRepository() : TokenContextRepository(
    tokenResolver = AccessTokenResolver()
) {

    override fun createAuthenticationFromToken(token: Token): Authentication? {
        if (token !is AccessToken) return null

        return token.makeAuthentication()
    }

}