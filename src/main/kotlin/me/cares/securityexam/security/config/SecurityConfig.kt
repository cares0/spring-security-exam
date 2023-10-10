package me.cares.securityexam.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.DispatcherType
import me.cares.securityexam.persistence.AccountRepository
import me.cares.securityexam.security.authentication.api.*
import me.cares.securityexam.security.requestwraping.ChangeToReReadableRequestFilter
import me.cares.securityexam.security.authentication.token.TokenAuthenticationEntrypoint
import me.cares.securityexam.security.authentication.token.TokenContextRepository
import me.cares.securityexam.security.authentication.token.TokenGenerator
import me.cares.securityexam.security.authentication.token.bearer.access.AccessTokenContextRepository
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
import org.springframework.web.filter.GenericFilterBean

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val objectMapper: ObjectMapper,
    private val accountRepository: AccountRepository,

    private val tokenGenerators: List<TokenGenerator>,

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

        http.addFilterBefore(changeToReReadableRequestFilter(), ApiAuthenticationFilter::class.java)

        http.securityContext { securityContext ->
            securityContext.securityContextRepository(tokenContextRepository())
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
        return ApiAuthenticationSuccessHandler(objectMapper, tokenGenerators)
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
    fun tokenContextRepository(): TokenContextRepository {
        return AccessTokenContextRepository()
    }

    @Bean
    fun authenticationEntrypoint(): AuthenticationEntryPoint {
        return TokenAuthenticationEntrypoint(objectMapper = objectMapper)
    }

}