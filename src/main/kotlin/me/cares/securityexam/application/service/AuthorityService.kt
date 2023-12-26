package me.cares.securityexam.application.service

import me.cares.securityexam.application.domain.AccountRole
import org.springframework.http.HttpMethod

interface AuthorityService {

    fun register(
        resourcePattern: String,
        requestMethod: HttpMethod,
        permittedRole: List<AccountRole>,
    )

}