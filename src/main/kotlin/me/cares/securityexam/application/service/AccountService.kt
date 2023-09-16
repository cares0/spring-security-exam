package me.cares.securityexam.application.service

import jakarta.transaction.Transactional
import me.cares.securityexam.application.domain.Account
import me.cares.securityexam.persistence.findByIdOrThrow
import me.cares.securityexam.persistence.AccountRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class AccountService(
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository,
) {

    fun joinAccount(
        email: String,
        rawPassword: String,
        name: String,
    ): UUID {
        val newAccount = Account.createNewAccount(
            email = email,
            encodedPassword = passwordEncoder.encode(rawPassword),
            name = name,
        )

        val savedAccount = accountRepository.save(newAccount)

        return savedAccount.id
    }

    fun upgradeToExpert(
        accountIdToUpgrade: String,
    ) {
        val accountToUpgrade = accountRepository.findByIdOrThrow(
            UUID.fromString(accountIdToUpgrade)
        )

        accountToUpgrade.upgradeExpert()
    }

}