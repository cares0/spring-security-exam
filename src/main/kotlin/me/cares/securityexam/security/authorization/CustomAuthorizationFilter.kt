package me.cares.securityexam.security.authorization

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.web.access.intercept.AuthorizationFilter

class CustomAuthorizationFilter(
    authorizationManager: AuthorizationManager<HttpServletRequest>
) : AuthorizationFilter(authorizationManager) {

}