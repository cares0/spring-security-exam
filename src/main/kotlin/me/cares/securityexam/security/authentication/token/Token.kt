package me.cares.securityexam.security.authentication.token

import com.auth0.jwt.interfaces.Claim

abstract class Token {

    abstract val value: String

    abstract val subject: String
    protected abstract val claims: Map<String, Claim>

    protected abstract val secretKey: String
    protected abstract val expirationTimeInSecond: Long

    open fun getType(): String {
        return this::class.simpleName!!
    }

}