package me.cares.securityexam.web.request

data class AccountJoinRequest(

    val email: String,
    val password: String,
    val name: String,

) {
}