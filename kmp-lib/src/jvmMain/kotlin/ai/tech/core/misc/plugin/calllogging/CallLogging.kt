package ai.tech.core.misc.plugin.calllogging

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.calllogging.model.config.CallLoggingConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import org.slf4j.event.Level

public fun Application.configureCallLogging(config: CallLoggingConfig?, block: (io.ktor.server.plugins.calllogging.CallLoggingConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.calllogging.CallLoggingConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            it.logging?.takeIf(EnabledConfig::enabled)?.let {
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

    if (configBlock == null && block == null) {
        return
    }

    install(CallLogging) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
