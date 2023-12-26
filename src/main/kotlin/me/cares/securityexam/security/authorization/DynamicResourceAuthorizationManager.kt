package me.cares.securityexam.security.authorization

import jakarta.servlet.DispatcherType
import jakarta.servlet.http.HttpServletRequest
import me.cares.securityexam.application.domain.AccountRole
import me.cares.securityexam.application.domain.ResourceAuthority
import me.cares.securityexam.persistence.ResourceAuthorityRepository
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authorization.AuthorityAuthorizationManager
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.DispatcherTypeRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import java.util.function.Supplier

class DynamicResourceAuthorizationManager(
    private val resourceAuthorityRepository: ResourceAuthorityRepository,
) : AuthorizationManager<HttpServletRequest> {

    private val permitAllManager = AuthorizationManager<RequestAuthorizationContext> { _, _ ->
        AuthorizationDecision(true)
    }

    override fun check(
        authentication: Supplier<Authentication>,
        request: HttpServletRequest?
    ): AuthorizationDecision? {
        val allResourceAuthorities = resourceAuthorityRepository.findAll()

        val delegate = buildDelegate(allResourceAuthorities)

        return delegate.check(authentication, request)
    }

    private fun buildDelegate(allResourceAuthorities: List<ResourceAuthority>): RequestMatcherDelegatingAuthorizationManager {
        val delegateBuilder = RequestMatcherDelegatingAuthorizationManager.builder()
        val roleHierarchy = initRoleHierarchy()

        allResourceAuthorities.forEach { resourceAuthority ->
            delegateBuilder.add(
                initRequestMatcher(resourceAuthority),
                initAuthorizationManager(resourceAuthority, roleHierarchy)
            )
        }

        initDefaultEntries(delegateBuilder)

        return delegateBuilder.build()
    }

    private fun initRoleHierarchy(): RoleHierarchy {
        val roleHierarchyImpl = RoleHierarchyImpl()
        roleHierarchyImpl.setHierarchy(
            AccountRole.values().joinToString("\n") {
                it.getRoleHierarchyExpression()
            }
        )

        return roleHierarchyImpl
    }

    private fun initRequestMatcher(resourceAuthority: ResourceAuthority): RequestMatcher {
        return AntPathRequestMatcher(
            resourceAuthority.extractResourcePattern(),
            resourceAuthority.extractRequestMethod()
        )
    }

    private fun initAuthorizationManager(
        resourceAuthority: ResourceAuthority,
        roleHierarchy: RoleHierarchy
    ): AuthorizationManager<RequestAuthorizationContext> {
        val authorityAuthorizationManager = AuthorityAuthorizationManager.hasAnyAuthority<RequestAuthorizationContext>(
            *resourceAuthority.permittedRole.map { it.name }.toTypedArray()
        )

        authorityAuthorizationManager.setRoleHierarchy(roleHierarchy)
        return authorityAuthorizationManager
    }

    private fun initDefaultEntries(delegateBuilder: RequestMatcherDelegatingAuthorizationManager.Builder) {
        delegateBuilder.add(
            DispatcherTypeRequestMatcher(DispatcherType.ERROR),
            permitAllManager
        )
        delegateBuilder.add(
            DispatcherTypeRequestMatcher(DispatcherType.FORWARD),
            permitAllManager
        )
    }

}