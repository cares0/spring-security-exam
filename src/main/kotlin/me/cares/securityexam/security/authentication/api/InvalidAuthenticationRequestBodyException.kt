package me.cares.securityexam.security.authentication.api

import org.springframework.security.core.AuthenticationException

class InvalidAuthenticationRequestBodyException(
    message: String?,
) : AuthenticationException(message)