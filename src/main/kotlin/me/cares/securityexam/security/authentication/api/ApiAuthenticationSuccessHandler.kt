package me.cares.securityexam.security.authentication.api

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.cares.securityexam.security.authentication.token.TokenGenerator
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class ApiAuthenticationSuccessHandler(
    private val objectMapper: ObjectMapper,
    private val tokenGenerators: List<TokenGenerator>
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()

        val tokens = tokenGenerators.map { generator ->
            generator.generate(authentication)
        }

        objectMapper.writeValue(
            response.writer,
            tokens.map { token ->
                ApiAuthenticationTokenResponse(
                    token = token.value,
                    type = token.getType()
                )
            }
        )
    }

}