package me.cares.securityexam.persistence

import me.cares.securityexam.application.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {

    @Query("""
        select a from Account a
        where a.email = :email
    """)
    fun findByEmailOrNull(@Param("email") email: String): Account?

}