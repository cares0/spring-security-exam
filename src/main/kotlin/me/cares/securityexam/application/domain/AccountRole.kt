package me.cares.securityexam.application.domain

enum class AccountRole(
    val upgradeEligibilityPeriodInDays: Long,
    private val childRoles: List<AccountRole>,
) {

    NORMAL(0, emptyList()),
    EXPERT(7, emptyList()),
    ADMIN(0, listOf(NORMAL, EXPERT)),

    ;

    fun getRoleHierarchyExpression(): String {
        return this.childRoles.joinToString("\n") { role ->
            "${this.name} > ${role.name}"
        }
    }

}