package ai.tech.core.misc.plugin.calllogging

import ai.tech.core.misc.plugin.calllogging.model.config.CallLoggingConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import org.slf4j.event.Level

public fun Application.configureCallLogging(config: CallLoggingConfig?) {
    config?.takeIf { it.enable != false }?.let {
        install(CallLogging) {
            it.logging?.let {
                it.level?.let { level = Level.valueOf(it) }
            }
            it.disableDefaultColors?.let {
                if (it) {
                    disableDefaultColors()
                }
            }
            it.disableForStaticContent?.let {
                if (it) {
                    disableForStaticContent()
                }
            }
        }
    }
}
