package me.cares.securityexam.persistence

import me.cares.securityexam.application.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
}