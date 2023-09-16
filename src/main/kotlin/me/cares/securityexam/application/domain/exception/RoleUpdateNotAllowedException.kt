package me.cares.securityexam.application.domain.exception

class RoleUpdateNotAllowedException(
    override val message: String
) : IllegalStateException() {
}