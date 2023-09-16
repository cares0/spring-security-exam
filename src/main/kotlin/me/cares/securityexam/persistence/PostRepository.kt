package me.cares.securityexam.persistence

import me.cares.securityexam.application.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
}