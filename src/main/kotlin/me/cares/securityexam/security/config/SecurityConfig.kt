package me.cares.securityexam.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import me.cares.securityexam.persistence.AccountRepository
import me.cares.securityexam.security.requestwraping.ChangeToReReadableRequestFilter
import me.cares.securityexam.security.authentication.ApiAuthenticationFailureHandler
import me.cares.securityexam.security.authentication.ApiAuthenticationFilter
import me.cares.securityexam.security.authentication.ApiAuthenticationSuccessHandler
import me.cares.securityexam.security.authentication.JpaUserDetailsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.GenericFilterBean

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val objectMapper: ObjectMapper,
    private val accountRepository: AccountRepository,

    @Value("\${spring.security.authentication.api.login-url}")
    private val apiAuthenticationUrl: String
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.formLogin { formLogin -> formLogin.disable() }
        http.csrf { csrf -> csrf.disable() }
        http.rememberMe { rememberMe -> rememberMe.disable() }
        http.httpBasic { httpBasic -> httpBasic.disable() }

        http.addFilterBefore(changeToReReadableRequestFilter(), ApiAuthenticationFilter::class.java)

        http.authorizeHttpRequests { authorize ->
            authorize.requestMatchers(apiAuthenticationUrl).permitAll()
            authorize.requestMatchers("accounts/join").permitAll()
            authorize.anyRequest().hasRole("USER")
        }

        http.authenticationManager(apiAuthenticationManager(http))

        http.apply(ApiAuthenticationConfigurer(objectMapper))
            .loginProcessingUrl(apiAuthenticationUrl)
            .successHandler(apiAuthenticationSuccessHandler())
            .failureHandler(apiAuthenticationFailureHandler())
            .authenticationDetailsSource(WebAuthenticationDetailsSource())

//        직접 필터 생성하는 경우
//        http.addFilterBefore(apiAuthenticationFilter(http), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

//    직접 필터 생성하는 경우
//    @Bean
//    fun apiAuthenticationFilter(http: HttpSecurity): ApiAuthenticationFilter {
//        val apiAuthenticationFilter = ApiAuthenticationFilter(objectMapper)
//
//        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
//        authenticationManagerBuilder.userDetailsService(jpaUserDetailsService())
//        val authenticationManager = authenticationManagerBuilder.build()
//
//        apiAuthenticationFilter.setAuthenticationManager(authenticationManager)
//        apiAuthenticationFilter.setRequiresAuthenticationRequestMatcher(
//            AntPathRequestMatcher(apiAuthenticationUrl, "POST")
//        )
//        apiAuthenticationFilter.setAuthenticationSuccessHandler(apiAuthenticationSuccessHandler())
//        apiAuthenticationFilter.setAuthenticationFailureHandler(apiAuthenticationFailureHandler())
//        return apiAuthenticationFilter
//    }

    @Bean
    fun apiAuthenticationManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.userDetailsService(jpaUserDetailsService())
        return authenticationManagerBuilder.build()
    }

    @Bean
    fun jpaUserDetailsService(): JpaUserDetailsService {
        return JpaUserDetailsService(accountRepository)
    }

    @Bean
    fun apiAuthenticationSuccessHandler(): AuthenticationSuccessHandler {
        return ApiAuthenticationSuccessHandler(objectMapper)
    }

    @Bean
    fun apiAuthenticationFailureHandler(): AuthenticationFailureHandler {
        return ApiAuthenticationFailureHandler(objectMapper)
    }

    @Bean
    fun changeToReReadableRequestFilter(): GenericFilterBean {
        return ChangeToReReadableRequestFilter()
    }

}