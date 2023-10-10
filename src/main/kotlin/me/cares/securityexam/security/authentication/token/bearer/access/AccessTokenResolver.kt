package me.cares.securityexam.security.authentication.token.bearer.access

import me.cares.securityexam.security.authentication.token.Token
import me.cares.securityexam.security.authentication.token.bearer.BearerTokenResolver
import org.springframework.http.HttpHeaders.AUTHORIZATION

class AccessTokenResolver(
    override val bearerTokenHeaderName: String = AUTHORIZATION,
    override val bearerTokenParameterName: String = "accessToken",
    override val allowFormEncodedBodyParameter: Boolean = false,
    override val allowUriQueryParameter: Boolean = false,
) : BearerTokenResolver() {

    override fun resolveSpecificToken(tokenStringValue: String): Token {
        return AccessToken.fromString(tokenStringValue)

    }

}