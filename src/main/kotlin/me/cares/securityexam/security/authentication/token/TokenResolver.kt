package me.cares.securityexam.security.authentication.token

import jakarta.servlet.http.HttpServletRequest

interface TokenResolver {

    fun resolve(request: HttpServletRequest): String?

    fun contains(request: HttpServletRequest): Boolean {
        return !resolve(request).isNullOrEmpty()
    }

}