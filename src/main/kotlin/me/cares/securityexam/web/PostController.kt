package me.cares.securityexam.web

import jakarta.servlet.http.HttpServletRequest
import me.cares.securityexam.application.service.PostService
import me.cares.securityexam.web.request.PostRegisterRequest
import me.cares.securityexam.web.request.PostUpdateRequest
import me.cares.securityexam.web.resolver.AccountId
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
) {

    @PostMapping("/questions")
    fun registerQuestion(
        @RequestBody request: PostRegisterRequest,
        @AccountId accountId: UUID,
    ): ApiResponse<Long> {
        val savedId = postService.registerQuestion(
            accountId = accountId,
            content = request.content
        )

        return ApiResponse.ofSuccess(savedId)
    }

    @PostMapping("/answers")
    fun registerAnswer(
        @RequestBody request: PostRegisterRequest,
        @AccountId accountId: UUID,
    ): ApiResponse<Long> {
        val savedId = postService.registerAnswer(
            accountId = accountId,
            content = request.content
        )

        return ApiResponse.ofSuccess(savedId)
    }

    @PatchMapping("/{postId}")
    fun updatePost(
        @PathVariable postId: Long,
        @RequestBody request: PostUpdateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<Any> {
        postService.updatePost(
            postId = postId,
            updatedContent = request.content
        )

        httpServletRequest.remoteAddr

        return ApiResponse.ofSuccess()
    }

    @DeleteMapping("/{postId}")
    fun deleteQuestion(
        @PathVariable postId: Long,
    ): ApiResponse<Any> {
        postService.deletePost(
            postId = postId,
        )

        return ApiResponse.ofSuccess()
    }

}