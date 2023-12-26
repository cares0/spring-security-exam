package me.cares.securityexam.application.service

import me.cares.securityexam.application.domain.AccountRole
import me.cares.securityexam.application.domain.ResourceAuthority
import me.cares.securityexam.persistence.ResourceAuthorityRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ResourceAuthorityService(
    private val resourceAuthorityRepository: ResourceAuthorityRepository,
): AuthorityService {

    override fun register(
        resourcePattern: String,
        requestMethod: HttpMethod,
        permittedRole: List<AccountRole>,
    ) {
        val newResourceAuthority = ResourceAuthority.of(
            resourcePattern = resourcePattern,
            requestMethod = requestMethod.name(),
            permittedRole = permittedRole,
        )

        val existResourceAuthority = resourceAuthorityRepository
            .findByIdOrNull(newResourceAuthority.fullResourcePattern)

        if (existResourceAuthority != null) {
            existResourceAuthority.update(
                permittedRole = permittedRole,
            )
        } else {
            resourceAuthorityRepository.save(newResourceAuthority)
        }
    }

}