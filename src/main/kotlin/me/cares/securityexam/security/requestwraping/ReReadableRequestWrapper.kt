package me.cares.securityexam.security.requestwraping

import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.apache.commons.io.IOUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


class ReReadableRequestWrapper(
    request: HttpServletRequest
) : HttpServletRequestWrapper(request) {

    private val encoding: Charset
    private val bodyData: ByteArray

    init {
        val characterEncoding = if (request.characterEncoding.isBlank()) {
            StandardCharsets.UTF_8.name()
        } else request.characterEncoding

        this.encoding = Charset.forName(characterEncoding)

        bodyData = IOUtils.toByteArray(super.getInputStream())
    }

    override fun getInputStream(): ServletInputStream {
        return BodyInputStream(bodyData)
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(this.inputStream, this.encoding))
    }

}