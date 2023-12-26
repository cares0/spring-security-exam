package me.cares.securityexam.web.request

import me.cares.securityexam.application.domain.AccountRole
import org.springframework.http.HttpMethod

data class AuthorityRegisterRequest(
    val resourcePattern: String,
    val requestMethod: HttpMethod,
    val permittedRole: List<AccountRole>,
) {
}