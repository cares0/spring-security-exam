package me.cares.securityexam.application.service

import jakarta.transaction.Transactional
import me.cares.securityexam.application.domain.Answer
import me.cares.securityexam.application.domain.Question
import me.cares.securityexam.persistence.findByIdOrThrow
import me.cares.securityexam.persistence.AccountRepository
import me.cares.securityexam.persistence.PostRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class PostService(
    private val accountRepository: AccountRepository,
    private val postRepository: PostRepository,
) {

    fun registerQuestion(
        accountId: UUID,
        content: String,
    ): Long {
        val writer = accountRepository.findByIdOrThrow(accountId)

        val newQuestion = Question.createNewQuestion(
            content = content,
            writer = writer,
        )

        val savedQuestion = postRepository.save(newQuestion)

        return savedQuestion.id
    }

    fun registerAnswer(
        accountId: UUID,
        content: String,
    ): Long {
        val writer = accountRepository.findByIdOrThrow(accountId)

        val newQuestion = Answer.createNewAnswer(
            content = content,
            expert = writer,
        )

        val savedQuestion = postRepository.save(newQuestion)

        return savedQuestion.id
    }

    fun updatePost(
        postId: Long,
        updatedContent: String,
    ) {
        val postToUpdate = postRepository.findByIdOrThrow(postId)
        postToUpdate.updateContent(updatedContent)
    }

    fun deletePost(
        postId: Long,
    ) {
        val postToDelete = postRepository.findByIdOrThrow(postId)
        postRepository.delete(postToDelete)
    }

}