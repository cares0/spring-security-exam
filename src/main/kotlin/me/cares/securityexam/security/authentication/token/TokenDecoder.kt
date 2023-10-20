package me.cares.securityexam.security.authentication.token

import me.cares.securityexam.security.authentication.api.CustomUserDetails

interface TokenDecoder {

    fun decodeToUserDetails(rawToken: String): CustomUserDetails

}