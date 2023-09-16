package me.cares.securityexam.web.resolver

class UnsupportedPrincipalException : IllegalStateException(
    "지원하지 않는 Principal 입니다."
) {
}