package me.cares.securityexam.security.authentication.token.exception

class UnavailableTokenException(
    cause: Exception? = null
) : TokenException(
    message = "사용할 수 없는 토큰입니다. 이미 토큰이 사용되었거나 유효하지 않습니다.",
    cause = cause
)