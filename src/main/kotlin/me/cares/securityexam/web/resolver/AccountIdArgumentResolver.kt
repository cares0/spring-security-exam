package me.cares.securityexam.web.resolver

import me.cares.securityexam.security.authentication.api.CustomUserDetails
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.UUID

@Component
class AccountIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasAnnotation = parameter.hasParameterAnnotation(AccountId::class.java)
        val isAssignableFrom = UUID::class.java.isAssignableFrom(parameter.parameterType)
        return hasAnnotation && isAssignableFrom
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val principal = SecurityContextHolder.getContext().authentication.principal

        return when (principal) {
            is UUID -> principal
            is CustomUserDetails -> principal.id
            is String -> UUID.fromString(principal)
            else -> throw UnsupportedPrincipalException()
        }
    }
}