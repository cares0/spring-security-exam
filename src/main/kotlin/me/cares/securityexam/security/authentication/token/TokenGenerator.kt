package me.cares.securityexam.security.authentication.token

import org.springframework.security.core.Authentication

interface TokenGenerator {

    fun generate(authentication: Authentication): Token

}