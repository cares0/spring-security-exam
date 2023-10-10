package me.cares.securityexam.security.authentication.token

class TokenExtractionException(
    cause: Exception? = null,
) : TokenException(
    message = "요청으로부터 토큰을 추출할 수 없습니다. 토큰이 누락되었거나, 토큰을 찾을 수 없는 경우에 발생합니다.",
    cause = cause
)