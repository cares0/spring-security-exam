package me.cares.securityexam.security.authentication.token

import me.cares.securityexam.security.authentication.api.CustomUserDetails
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class TokenBasedAuthentication(

    val type: Token,
    val rawToken: String,
    private var isDecoded: Boolean,
    private val customUserDetails: CustomUserDetails? = null,

) : Authentication {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return customUserDetails?.authorities ?: emptySet()
    }

    override fun getName(): String {
        return rawToken
    }

    override fun getCredentials(): Any {
        return rawToken
    }

    override fun getDetails(): Any {
        return customUserDetails ?: rawToken
    }

    override fun getPrincipal(): Any {
        return details
    }

    override fun isAuthenticated(): Boolean {
        return isDecoded
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        isDecoded = isAuthenticated
    }

    companion object {

        fun beforeAuthentication(
            tokenType: Token,
            rawToken: String,
        ): Authentication {
            return TokenBasedAuthentication(
                type = tokenType,
                rawToken = rawToken,
                isDecoded = false,
            )
        }

        fun successfulAuthentication(
            tokenType: Token,
            rawToken: String,
            userDetails: CustomUserDetails,
        ): Authentication {
            return TokenBasedAuthentication(
                type = tokenType,
                rawToken = rawToken,
                isDecoded = false,
                customUserDetails = userDetails,
            )
        }

    }

}