package me.cares.securityexam.security.authentication.token

import me.cares.securityexam.security.authentication.api.CustomUserDetails

interface TokenGenerator {

    fun generateFromUserDetails(customUserDetails: CustomUserDetails): String

}