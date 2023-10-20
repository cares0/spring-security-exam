package me.cares.securityexam.security.authentication.token

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.cares.securityexam.security.authentication.token.exception.ExpiredTokenException
import me.cares.securityexam.security.authentication.token.exception.TokenExtractionException
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

class TokenAuthenticationFilter(

    private val accessTokenResolver: TokenResolver,
    private val refreshTokenResolver: TokenResolver,

) : AbstractAuthenticationProcessingFilter(
    AntPathRequestMatcher.antMatcher("/**")
) {

    override fun requiresAuthentication(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        return super.requiresAuthentication(request, response) && isTokenExist(request)
    }

    private fun isTokenExist(request: HttpServletRequest) =
        accessTokenResolver.contains(request) || refreshTokenResolver.contains(request)

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        val rawAccessToken = resolveAccessToken(request)

        val accessTokenAuthentication = makeBeforeAuthentication(rawAccessToken, Token.ACCESS)

        return authenticationManager.authenticate(accessTokenAuthentication)
    }

    private fun resolveAccessToken(request: HttpServletRequest) =
        accessTokenResolver.resolve(request) ?: throw TokenExtractionException()

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain?,
        authResult: Authentication
    ) {
        val tokenBasedAuthentication = authResult as TokenBasedAuthentication

        when (tokenBasedAuthentication.type) {
            Token.ACCESS -> handleSuccessfulAccessTokenAuthentication(request, response, chain, authResult)
            Token.REFRESH -> handleSuccessfulRefreshTokenAuthentication(request, response, chain, authResult)
        }
    }

    private fun handleSuccessfulRefreshTokenAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain?,
        authResult: Authentication
    ) {
        super.successfulAuthentication(request, response, chain, authResult)
    }

    private fun handleSuccessfulAccessTokenAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain?,
        authResult: Authentication,
    ) {
        setAuthenticationToSecurityContext(authResult)

        publishEvent(authResult)

        chain!!.doFilter(request, response)
    }

    private fun publishEvent(authResult: Authentication) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(InteractiveAuthenticationSuccessEvent(authResult, this.javaClass))
        }
    }

    private fun setAuthenticationToSecurityContext(authResult: Authentication) {
        val securityContext = SecurityContextHolder.getContext()
        securityContext.authentication = authResult
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failedReason: AuthenticationException
    ) {
        when (failedReason) {
            is ExpiredTokenException,
            is TokenExtractionException -> {
                try {
                    val refreshTokenAuthResult = authenticateByRefreshToken(request)

                    successfulAuthentication(request, response, null, refreshTokenAuthResult)
                } catch (e: AuthenticationException) {
                    super.unsuccessfulAuthentication(request, response, e)
                }
            }
            else -> super.unsuccessfulAuthentication(request, response, failedReason)
        }
    }

    private fun authenticateByRefreshToken(
        request: HttpServletRequest,
    ): Authentication {
        val rawRefreshToken = resolveRefreshToken(request)

        val refreshTokenAuthentication = makeBeforeAuthentication(rawRefreshToken, Token.REFRESH)

        return authenticationManager.authenticate(refreshTokenAuthentication)
    }

    private fun resolveRefreshToken(request: HttpServletRequest) = (refreshTokenResolver.resolve(request)
        ?: throw TokenExtractionException())

    private fun makeBeforeAuthentication(rawToken: String, tokenType: Token): Authentication {
        return TokenBasedAuthentication.beforeAuthentication(
            tokenType = tokenType,
            rawToken = rawToken
        )
    }
}