package me.cares.securityexam.application.domain

import jakarta.persistence.*
import jakarta.persistence.EnumType.*
import jakarta.persistence.GenerationType.*
import me.cares.securityexam.application.domain.exception.RoleUpdateNotAllowedException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
class Account private constructor(

    val name: String,
    val password: String,
    val email: String,

    @Enumerated(STRING)
    var role: AccountRole = AccountRole.NORMAL,

) {

    @Id
    @GeneratedValue(strategy = UUID)
    @Column(name = "account_id", insertable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

    val joinDateTime: LocalDateTime = LocalDateTime.now()

    fun upgradeExpert() {
        val requiredJoinPeriodInDays = AccountRole.EXPERT.upgradeEligibilityPeriodInDays

        if (!isJoinDateBefore(requiredJoinPeriodInDays))
            throw RoleUpdateNotAllowedException(
                "전문가 계정은 가입한 지 ${requiredJoinPeriodInDays}일이 지난 계정만 업데이트 가능합니다."
            )

        this.role = AccountRole.EXPERT
    }

    private fun isJoinDateBefore(daysAgo: Long): Boolean {
        val joinDate = this.joinDateTime.toLocalDate()
        val dateToValidate = LocalDate.now().minusDays(daysAgo)

        return joinDate.isBefore(dateToValidate) || joinDate.isEqual(dateToValidate)
    }

    companion object {

        fun createNewAccount(
            email: String,
            name: String,
            encodedPassword: String,
        ): Account {

            return Account(
                name = name,
                password = encodedPassword,
                email = email,
                role = getRoleFromEmail(email)
            )

        }

        private fun getRoleFromEmail(email: String): AccountRole {
            val emailId = email.substringBeforeLast("@")
            val emailDomain = email.substringAfterLast("@")

            return if (emailId.endsWith(".admin") && emailDomain == "security.com")
                AccountRole.ADMIN
            else AccountRole.NORMAL
        }

    }

}