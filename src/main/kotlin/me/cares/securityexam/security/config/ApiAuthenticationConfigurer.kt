package me.cares.securityexam.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import me.cares.securityexam.security.authentication.ApiAuthenticationFilter
import org.springframework.security.config.annotation.web.HttpSecurityBuilder
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

class ApiAuthenticationConfigurer<H: HttpSecurityBuilder<H>>(
    objectMapper: ObjectMapper,
) : AbstractAuthenticationFilterConfigurer<H, ApiAuthenticationConfigurer<H>, ApiAuthenticationFilter>(
    ApiAuthenticationFilter(objectMapper), "/accounts/login"
) {

    override fun createLoginProcessingUrlMatcher(loginProcessingUrl: String?): RequestMatcher {
        return AntPathRequestMatcher(loginProcessingUrl, "POST")
    }

}