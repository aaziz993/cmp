package ai.tech.core.misc.plugin.shutdown

import ai.tech.core.misc.plugin.shutdown.model.config.ShutDownConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*

public fun Application.configureShutdown(config: ShutDownConfig?) =
    config?.takeIf { it.enable != false }?.let {
        install(ShutDownUrl.ApplicationCallPlugin) {
            config.shutDownUrl?.let { shutDownUrl = it }
            config.exitCodeSupplier?.let { exitCodeSupplier = { it } }
        }
    }
