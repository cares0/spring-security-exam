package me.cares.securityexam.security.authentication.token.exception

class InvalidTokenCookieException(
    cause: Exception? = null
) : TokenException(
    message = "토큰 쿠키가 유효하지 않습니다.",
    cause = cause
)