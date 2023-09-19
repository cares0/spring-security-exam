package me.cares.securityexam.security.requestwraping

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class BodyInputStream(body: ByteArray) : ServletInputStream() {
    private val delegate: InputStream

    init {
        delegate = ByteArrayInputStream(body)
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun setReadListener(readListener: ReadListener) {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return delegate.read()
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return delegate.read(b, off, len)
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        return delegate.read(b)
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        return delegate.skip(n)
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return delegate.available()
    }

    @Throws(IOException::class)
    override fun close() {
        delegate.close()
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        delegate.mark(readlimit)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        delegate.reset()
    }

    override fun markSupported(): Boolean {
        return delegate.markSupported()
    }
}
