package ai.tech.core.misc.plugin

import core.io.model.http.server.callloging.CallLoggingConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLoggingConfig
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

public fun Application.configCallLogging(config: CallLoggingConfig?) {
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
