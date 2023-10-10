package me.cares.securityexam.security.authentication.token.bearer

import jakarta.servlet.http.HttpServletRequest
import me.cares.securityexam.security.authentication.token.*
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import java.util.regex.Pattern

abstract class BearerTokenResolver : TokenResolver {

    private val authorizationPattern = Pattern.compile(
        "^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
        Pattern.CASE_INSENSITIVE
    )

    abstract val bearerTokenHeaderName: String
    abstract val bearerTokenParameterName: String
    abstract val allowFormEncodedBodyParameter: Boolean
    abstract val allowUriQueryParameter: Boolean

    override fun resolve(request: HttpServletRequest): Token? {
        val tokenStringValue = resolveTokenStringValue(request)

        return if (tokenStringValue == null) null
        else resolveSpecificToken(tokenStringValue)
    }

    abstract fun resolveSpecificToken(tokenStringValue: String): Token

    private fun resolveTokenStringValue(request: HttpServletRequest): String? {
        val authorizationHeaderToken = resolveFromAuthorizationHeader(request)
        val parameterToken =
            if (isParameterTokenSupportedForRequest(request)) resolveFromRequestParameters(request)
            else null
        return if (authorizationHeaderToken != null) {
            if (parameterToken != null) throw MultipleTokensException()
            else authorizationHeaderToken
        } else {
            if (parameterToken != null && isParameterTokenEnabledForRequest(request)) parameterToken
            else null
        }
    }

    private fun resolveFromAuthorizationHeader(request: HttpServletRequest): String? {
        val authorization = request.getHeader(bearerTokenHeaderName) ?: return null
        if (!authorization.startsWith("bearer", true)) return null
        val matcher = authorizationPattern.matcher(authorization)
        return if (!matcher.matches()) throw TokenTypeMismatchException()
        else matcher.group("token")
    }

    private fun resolveFromRequestParameters(request: HttpServletRequest): String? {
        val values = request.getParameterValues(bearerTokenParameterName)
        return if (values == null || values.isEmpty()) null
        else if (values.size == 1) values[0]
        else throw MultipleTokensException()
    }

    private fun isParameterTokenSupportedForRequest(request: HttpServletRequest): Boolean {
        return isFormEncodedRequest(request) || isGetRequest(request)
    }

    private fun isGetRequest(request: HttpServletRequest): Boolean {
        return HttpMethod.GET.name() == request.method
    }

    private fun isFormEncodedRequest(request: HttpServletRequest): Boolean {
        return MediaType.APPLICATION_FORM_URLENCODED_VALUE == request.contentType
    }

    private fun hasAccessTokenInQueryString(request: HttpServletRequest): Boolean {
        return request.queryString != null && request.queryString.contains(bearerTokenParameterName)
    }

    private fun isParameterTokenEnabledForRequest(request: HttpServletRequest): Boolean {
        return (allowFormEncodedBodyParameter && isFormEncodedRequest(request) && !isGetRequest(request)
                && !hasAccessTokenInQueryString(request)) || allowUriQueryParameter && isGetRequest(request)
    }

}