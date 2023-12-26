package me.cares.securityexam.web

import me.cares.securityexam.application.service.AuthorityService
import me.cares.securityexam.web.request.AuthorityRegisterRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authorities")
class AuthorityController(
    private val authorityService: AuthorityService,
) {

    @PostMapping
    fun register(
        @RequestBody request: AuthorityRegisterRequest,
    ): ApiResponse<Any> {
        authorityService.register(
            resourcePattern = request.resourcePattern,
            requestMethod = request.requestMethod,
            permittedRole = request.permittedRole,
        )

        return ApiResponse.ofSuccess()
    }

}