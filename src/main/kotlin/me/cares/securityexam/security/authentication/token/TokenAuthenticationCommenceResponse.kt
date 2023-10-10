package me.cares.securityexam.security.authentication.token

data class TokenAuthenticationCommenceResponse(
    val error: String,
    val cause: String?,
)