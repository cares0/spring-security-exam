package me.cares.securityexam.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.cares.securityexam.web.ApiResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class TokenAuthenticationEntrypoint(
    private val realmName: String = "exam-app",
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val parameters: MutableMap<String, String> = mutableMapOf()

        parameters["realm"] = realmName
        parameters["error"] = authException::class.simpleName!!

        val wwwAuthenticateValue = computeWWWAuthenticateHeaderValue(parameters)

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticateValue)
        objectMapper.writeValue(
            response.writer,
            ApiResponse.ofError(
                HttpStatus.UNAUTHORIZED,
                data = authException.message
            )
        )
    }

    private fun computeWWWAuthenticateHeaderValue(parameters: Map<String, String>): String {
        val wwwAuthenticate = StringBuilder()
        wwwAuthenticate.append("Bearer")
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ")
            var i = 0
            for ((key, value) in parameters) {
                wwwAuthenticate.append(key).append("=\"").append(value).append("\"")
                if (i != parameters.size - 1) {
                    wwwAuthenticate.append(", ")
                }
                i++
            }
        }
        return wwwAuthenticate.toString()
    }

}