package me.cares.securityexam.security.authentication.token

import jakarta.servlet.http.HttpServletRequest

interface TokenResolver {

    fun resolve(request: HttpServletRequest): Token?

}