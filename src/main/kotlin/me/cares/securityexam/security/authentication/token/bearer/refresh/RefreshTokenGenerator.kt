package me.cares.securityexam.security.authentication.token.bearer.refresh

import me.cares.securityexam.security.authentication.token.Token
import me.cares.securityexam.security.authentication.token.bearer.BearerTokenGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component

@Component
class RefreshTokenGenerator() : BearerTokenGenerator() {

    override fun generate(authentication: Authentication): Token {
        return RefreshToken.generateNew(
            accountId = authentication.name,
        )
    }

}