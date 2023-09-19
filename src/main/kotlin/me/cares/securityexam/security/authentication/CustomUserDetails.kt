package me.cares.securityexam.security.authentication

import me.cares.securityexam.application.domain.AccountRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomUserDetails(

    val email: String,

    @get: JvmName("getPasswordKt")
    val password: String,

    val role: AccountRole,

) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return setOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}