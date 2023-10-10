package me.cares.securityexam.security.authentication.token

import org.springframework.security.core.AuthenticationException

abstract class TokenException(
    override val message: String?,
    override val cause: Throwable? = null,
) : AuthenticationException(
    message, cause
)