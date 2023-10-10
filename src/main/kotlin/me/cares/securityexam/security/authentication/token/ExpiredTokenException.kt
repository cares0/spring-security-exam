package me.cares.securityexam.security.authentication.token

class ExpiredTokenException(
    cause: Exception? = null
) : TokenException(
    message = "만료된 토큰입니다.",
    cause = cause
)