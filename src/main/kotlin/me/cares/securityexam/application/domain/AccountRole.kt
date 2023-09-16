package me.cares.securityexam.application.domain

enum class AccountRole(
    val upgradeEligibilityPeriodInDays: Long
) {

    NORMAL(0),
    EXPERT(7),
    ADMIN(0),

}