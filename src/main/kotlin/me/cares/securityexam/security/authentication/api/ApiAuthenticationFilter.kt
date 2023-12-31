package me.cares.securityexam.security.authentication.api

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class ApiAuthenticationFilter(
    private val objectMapper: ObjectMapper,
) : UsernamePasswordAuthenticationFilter() {

    override fun obtainPassword(request: HttpServletRequest): String? {
        return obtainLoginRequest(request).password
    }

    override fun obtainUsername(request: HttpServletRequest): String? {
        return obtainLoginRequest(request).email
    }

    private fun obtainLoginRequest(request: HttpServletRequest): ApiAuthenticationRequest {
        return try {
            objectMapper.readValue(request.reader, ApiAuthenticationRequest::class.java)
        } catch (e: Exception) {
            throw InvalidAuthenticationRequestBodyException(e.message)
        }
    }

}