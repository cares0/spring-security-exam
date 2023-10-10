package me.cares.securityexam.security.authentication.token

class TokenTypeMismatchException(
    cause: Exception? = null
) : TokenException(
    message = "토큰의 타입이 맞지 않습니다.",
    cause = cause
)