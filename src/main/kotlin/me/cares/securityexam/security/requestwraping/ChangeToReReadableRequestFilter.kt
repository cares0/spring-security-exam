package me.cares.securityexam.security.requestwraping

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class ChangeToReReadableRequestFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val readableRequestWrapper = ReReadableRequestWrapper(request)
        filterChain.doFilter(readableRequestWrapper, response)
    }

}