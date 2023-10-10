package me.cares.securityexam.security.authentication.api

data class ApiAuthenticationTokenResponse(
    val token: String,
    val type: String,
) {
}