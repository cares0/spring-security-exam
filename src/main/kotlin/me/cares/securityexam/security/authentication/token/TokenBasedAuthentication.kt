package me.cares.securityexam.security.authentication.token

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.UUID

class TokenBasedAuthentication private constructor(
    private val id: UUID? = null,
    private val role: List<String>? = null,
    private val token: Token? = null,

    private var authenticated: Boolean = true,
    private val tokenExceptionHolder: TokenException? = null,
) : Authentication {

    override fun getName(): String {
        return if (authenticated) id!!.toString()
        else throw tokenExceptionHolder!!
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return if (authenticated) role!!.map { SimpleGrantedAuthority(it) }
        else throw tokenExceptionHolder!!
    }

    override fun getCredentials(): Any {
        return if (authenticated) token!!.value
        else throw tokenExceptionHolder!!
    }

    override fun getDetails(): Any {
        return if (authenticated) token!!
        else throw tokenExceptionHolder!!
    }

    override fun getPrincipal(): Any {
        return if (authenticated) id!!.toString()
        else throw tokenExceptionHolder!!
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.authenticated = isAuthenticated
    }

    companion object {

        fun unAuthenticated(
            tokenException: TokenException,
        ): Authentication {
            return TokenBasedAuthentication(
                authenticated = false,
                tokenExceptionHolder = tokenException
            )
        }

        fun authenticated(
            id: UUID,
            role: List<String>,
            token: Token,
        ): Authentication {
            return TokenBasedAuthentication(
                id = id,
                role = role,
                token = token
            )
        }

    }

}