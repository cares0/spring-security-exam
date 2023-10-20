package me.cares.securityexam.security.authentication.api

data class ApiAuthenticationRequest(
    val email: String,
    val password: String,
)