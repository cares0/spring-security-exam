package me.cares.securityexam.security.authentication

data class ApiAuthenticationRequest(
    val email: String,
    val password: String,
)