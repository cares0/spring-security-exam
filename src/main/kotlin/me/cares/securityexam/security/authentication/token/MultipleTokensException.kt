package me.cares.securityexam.security.authentication.token

class MultipleTokensException(
    cause: Exception? = null
) : TokenException(
    message = "토큰이 여러 개 제공되었습니다.",
    cause = cause
)