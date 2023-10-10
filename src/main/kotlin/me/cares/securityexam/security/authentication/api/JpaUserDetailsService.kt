package me.cares.securityexam.security.authentication.api

import me.cares.securityexam.persistence.AccountRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JpaUserDetailsService(
    private val accountRepository: AccountRepository,
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val account = accountRepository.findByEmailOrNull(email)
            ?: throw UsernameNotFoundException(email)

        return CustomUserDetails(
            id = account.id,
            email = account.email,
            password = account.password,
            role = account.role,
        )
    }

}