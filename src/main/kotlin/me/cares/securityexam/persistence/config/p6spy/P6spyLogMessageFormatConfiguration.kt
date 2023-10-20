package me.cares.securityexam.persistence.config.p6spy

import com.p6spy.engine.spy.P6SpyOptions
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class P6spyLogMessageFormatConfiguration {

    @PostConstruct
    fun setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().logMessageFormat =
            CustomP6spySqlFormatter::class.java.name
    }
}