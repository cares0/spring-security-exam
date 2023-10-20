package me.cares.securityexam.security.authentication.token.exception

class TokenNotDecodedException() : IllegalStateException(
    "아직 디코딩되지 않은 토큰입니다."
) {
}