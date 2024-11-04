package ai.tech.core.misc.plugin.shutdown

import ai.tech.core.misc.plugin.shutdown.model.config.ShutDownConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*

public fun Application.configureShutdown(config: ShutDownConfig?, block: (ShutDownUrl.Config.() -> Unit)? = null) {
    val configBlock: (ShutDownUrl.Config.() -> Unit)? = config?.takeIf { it.enable != false }?.let {
        {
            it.shutDownUrl?.let { shutDownUrl = it }
            it.exitCodeSupplier?.let { exitCodeSupplier = { it } }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(ShutDownUrl.ApplicationCallPlugin) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
