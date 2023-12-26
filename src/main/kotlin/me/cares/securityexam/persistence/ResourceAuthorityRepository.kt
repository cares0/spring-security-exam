package me.cares.securityexam.persistence

import me.cares.securityexam.application.domain.ResourceAuthority
import org.springframework.data.jpa.repository.JpaRepository

interface ResourceAuthorityRepository : JpaRepository<ResourceAuthority, String> {
}