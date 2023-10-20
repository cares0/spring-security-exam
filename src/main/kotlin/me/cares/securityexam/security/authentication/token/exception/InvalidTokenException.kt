package me.cares.securityexam.security.authentication.token.exception

class InvalidTokenException(
    cause: Exception? = null
) : TokenException(
    message = "유효하지 않은 토큰입니다. 서버에서 발급된 토큰인지 확인해주세요.",
    cause = cause
)