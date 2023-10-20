package me.cares.securityexam.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.DispatcherType
import me.cares.securityexam.persistence.AccountRepository
import me.cares.securityexam.persistence.RefreshTokenRepository
import me.cares.securityexam.security.TokenAuthenticationEntrypoint
import me.cares.securityexam.security.authentication.api.*
import me.cares.securityexam.security.authentication.token.*
import me.cares.securityexam.security.requestwraping.ChangeToReReadableRequestFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.filter.GenericFilterBean

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val objectMapper: ObjectMapper,
    private val accountRepository: AccountRepository,
    private val refreshTokenRepository: RefreshTokenRepository,

    private val accessTokenManager: AccessTokenManager,
    private val refreshTokenManager: RefreshTokenManager,

    @Value("\${spring.security.authentication.api.login-url}")
    private val apiAuthenticationUrl: String
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.formLogin { formLogin -> formLogin.disable() }
        http.csrf { csrf -> csrf.disable() }
        http.rememberMe { rememberMe -> rememberMe.disable() }
        http.httpBasic { httpBasic -> httpBasic.disable() }

        http.sessionManagement { sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        http.authorizeHttpRequests { authorize ->
            authorize.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
            authorize.requestMatchers(apiAuthenticationUrl).permitAll()
            authorize.requestMatchers("/accounts/join").permitAll()
            authorize.anyRequest().hasAuthority("NORMAL")
        }

        http.authenticationManager(apiAuthenticationManager(http))

        http.apply(ApiAuthenticationConfigurer(objectMapper))
            .loginProcessingUrl(apiAuthenticationUrl)
            .successHandler(apiAuthenticationSuccessHandler())
            .failureHandler(apiAuthenticationFailureHandler())
            .authenticationDetailsSource(WebAuthenticationDetailsSource())


        http.exceptionHandling { exceptionHandling ->
            exceptionHandling.authenticationEntryPoint(authenticationEntrypoint())
        }

        http.addFilterBefore(changeToReReadableRequestFilter(), ApiAuthenticationFilter::class.java)
        http.addFilterAfter(tokenAuthenticationFilter(http), ApiAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun tokenAuthenticationFilter(http: HttpSecurity): TokenAuthenticationFilter {
        val tokenAuthenticationFilter = TokenAuthenticationFilter(
            accessTokenResolver = accessTokenManager,
            refreshTokenResolver = refreshTokenManager,
        )

        tokenAuthenticationFilter.setAuthenticationManager(apiAuthenticationManager(http))
        tokenAuthenticationFilter.setAuthenticationSuccessHandler(apiAuthenticationSuccessHandler())
        tokenAuthenticationFilter.setAuthenticationFailureHandler(apiAuthenticationFailureHandler())

        return tokenAuthenticationFilter
    }

    @Bean
    fun apiAuthenticationManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.userDetailsService(jpaUserDetailsService())
        authenticationManagerBuilder.authenticationProvider(tokenAuthenticationProvider())
        return authenticationManagerBuilder.build()
    }

    @Bean
    fun tokenAuthenticationProvider(): TokenAuthenticationProvider {
        return TokenAuthenticationProvider(
            accessTokenDecoder = accessTokenManager,
            refreshTokenDecoder = refreshTokenManager,
        )
    }

    @Bean
    fun jpaUserDetailsService(): JpaUserDetailsService {
        return JpaUserDetailsService(accountRepository)
    }

    @Bean
    fun apiAuthenticationSuccessHandler(): AuthenticationSuccessHandler {
        return ApiAuthenticationSuccessHandler(
            apiLoginRequestMatcher = AntPathRequestMatcher(apiAuthenticationUrl, "POST"),
            objectMapper = objectMapper,
            accessTokenGenerator = accessTokenManager,
            refreshTokenGenerator = refreshTokenManager,
            refreshTokenRepository = refreshTokenRepository,
            accountRepository = accountRepository,
        )
    }

    @Bean
    fun apiAuthenticationFailureHandler(): AuthenticationFailureHandler {
        return ApiAuthenticationFailureHandler(objectMapper)
    }

    @Bean
    fun changeToReReadableRequestFilter(): GenericFilterBean {
        return ChangeToReReadableRequestFilter()
    }

    @Bean
    fun authenticationEntrypoint(): AuthenticationEntryPoint {
        return TokenAuthenticationEntrypoint(objectMapper = objectMapper)
    }

}