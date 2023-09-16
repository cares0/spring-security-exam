package me.cares.securityexam.web

import org.springframework.http.HttpStatus

data class ApiResponse<T> private constructor(
    val status: String,
    val data: T?
) {

    companion object {

        fun <T> ofSuccess(data: T? = null): ApiResponse<T> {
            return ApiResponse(
                status = HttpStatus.OK.name,
                data = data
            )
        }

    }
}