package me.cares.securityexam.security.authentication.token.bearer.access

import me.cares.securityexam.security.authentication.token.Token
import me.cares.securityexam.security.authentication.token.bearer.BearerTokenGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component

@Component
class AccessTokenGenerator() : BearerTokenGenerator() {

    override fun generate(authentication: Authentication): Token {
        return AccessToken.generateNew(
            accountId = authentication.name,
            role = authentication.authorities.map(GrantedAuthority::getAuthority)
        )
    }

}