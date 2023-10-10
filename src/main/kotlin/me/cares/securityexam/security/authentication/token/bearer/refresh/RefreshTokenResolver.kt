package me.cares.securityexam.security.authentication.token.bearer.refresh

import me.cares.securityexam.security.authentication.token.Token
import me.cares.securityexam.security.authentication.token.bearer.BearerTokenResolver
import org.springframework.http.HttpHeaders.AUTHORIZATION

class RefreshTokenResolver(
    override val bearerTokenHeaderName: String = AUTHORIZATION,
    override val bearerTokenParameterName: String = "refreshToken",
    override val allowFormEncodedBodyParameter: Boolean = false,
    override val allowUriQueryParameter: Boolean = true,
) : BearerTokenResolver() {

    override fun resolveSpecificToken(tokenStringValue: String): Token {
        return RefreshToken.fromString(tokenStringValue)
    }

}