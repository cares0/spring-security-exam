package me.cares.securityexam.application.domain

import jakarta.persistence.*
import jakarta.persistence.FetchType.*
import jakarta.persistence.GenerationType.*
import jakarta.persistence.InheritanceType.*
import java.time.LocalDateTime

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "post_type")
abstract class Post(

    var content: String,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    val writer: Account,

) {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id", insertable = false, updatable = false)
    val id: Long = 0

    @Column(name = "post_type", insertable = false, updatable = false)
    @Enumerated
    val type: PostType = PostType.QUESTION

    val postDateTime: LocalDateTime = LocalDateTime.now()

    var lastUpdateDateTime: LocalDateTime = LocalDateTime.now()

    fun updateContent(updatedContent: String) {
        this.content = updatedContent
        this.lastUpdateDateTime = LocalDateTime.now()
    }

}