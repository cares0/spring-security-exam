package me.cares.securityexam.web

import me.cares.securityexam.application.domain.Account
import me.cares.securityexam.application.service.AccountService
import me.cares.securityexam.persistence.AccountRepository
import me.cares.securityexam.web.request.AccountJoinRequest
import me.cares.securityexam.web.request.AccountRoleUpgradeRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService,
) {

    @PostMapping("/join")
    fun joinAccount(
        @RequestBody request: AccountJoinRequest,
    ): ApiResponse<UUID> {
        val savedId = accountService.joinAccount(
            email = request.email,
            rawPassword = request.password,
            name = request.name,
        )

        return ApiResponse.ofSuccess(savedId)
    }

    @PostMapping("/upgrade-expert")
    fun upgradeToExpert(
        @RequestBody request: AccountRoleUpgradeRequest,
    ): ApiResponse<Any> {
        accountService.upgradeToExpert(request.targetAccountId)

        return ApiResponse.ofSuccess()
    }

}