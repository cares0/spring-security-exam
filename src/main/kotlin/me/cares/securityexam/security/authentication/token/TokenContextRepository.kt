package me.cares.securityexam.security.authentication.token

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.web.context.SecurityContextRepository

abstract class TokenContextRepository(
    private val tokenResolver: TokenResolver,

    protected val securityContextHolderStrategy: SecurityContextHolderStrategy =
        SecurityContextHolder.getContextHolderStrategy(),

    private val requestAttributeName: String = DEFAULT_REQUEST_ATTR_NAME
) : SecurityContextRepository {

    override fun loadContext(requestResponseHolder: HttpRequestResponseHolder): SecurityContext? {

        val request = requestResponseHolder.request

        return getContextFromRequestAttribute(request) ?: getContextFromToken(request)
    }

    private fun getContextFromRequestAttribute(request: HttpServletRequest) =
        request.getAttribute(this.requestAttributeName) as? SecurityContext

    private fun getContextFromToken(request: HttpServletRequest): SecurityContext? {
        val newContext = generateNewContext()

        try {
            val token = tokenResolver.resolve(request) ?: return null
            newContext.authentication = createAuthenticationFromToken(token)
        } catch (e: TokenException) {
            newContext.authentication = TokenBasedAuthentication.unAuthenticated(e)
        } catch (e: Throwable) {
            return null
        }

        return newContext
    }

    private fun generateNewContext(): SecurityContext {
        return securityContextHolderStrategy.createEmptyContext()
    }

    abstract fun createAuthenticationFromToken(token: Token): Authentication?

    override fun saveContext(context: SecurityContext, request: HttpServletRequest, response: HttpServletResponse) {
        request.setAttribute(requestAttributeName, context)
    }

    override fun containsContext(request: HttpServletRequest): Boolean {
        return getContextFromRequestAttribute(request) != null
                || getContextFromToken(request) != null
    }

    companion object {
        val DEFAULT_REQUEST_ATTR_NAME =
            "${this::class.java.name}.SPRING_SECURITY_CONTEXT"
    }

}