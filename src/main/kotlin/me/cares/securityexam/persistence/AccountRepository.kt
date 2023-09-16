package me.cares.securityexam.persistence

import me.cares.securityexam.application.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {
}