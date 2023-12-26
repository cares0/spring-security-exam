package me.cares.securityexam.application.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Transient

@Entity
class ResourceAuthority(

    _resourcePattern: String,
    _requestMethod: String,
    _permittedRole: List<AccountRole>,

) {

    @Id
    val fullResourcePattern: String = "$_requestMethod $_resourcePattern"

    private var permittedRoleJoined: String = joinByComma(_permittedRole)

    val permittedRole: List<AccountRole>
        get() = splitByComma(permittedRoleJoined).map(AccountRole::valueOf)

    fun update(
        permittedRole: List<AccountRole>,
    ) {
        permittedRoleJoined = joinByComma(permittedRole)
    }

    fun extractRequestMethod(): String {
        return fullResourcePattern.substringBefore(" ")
    }

    fun extractResourcePattern(): String {
        return fullResourcePattern.substringAfter(" ")
    }

    private fun joinByComma(list: List<Any>) = list.joinToString(", ")

    private fun splitByComma(string: String) = string.split(", ")

    companion object {

        fun of(
            resourcePattern: String,
            requestMethod: String,
            permittedRole: List<AccountRole>,
        ): ResourceAuthority {
            return ResourceAuthority(
                _resourcePattern = resourcePattern,
                _requestMethod = requestMethod,
                _permittedRole = permittedRole,
            )
        }

    }

}