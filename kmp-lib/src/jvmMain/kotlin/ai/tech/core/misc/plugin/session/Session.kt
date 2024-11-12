package ai.tech.core.misc.plugin.session

import ai.tech.core.misc.plugin.auth.model.config.AuthProvidersConfig
import io.ktor.server.application.*
import io.ktor.server.sessions.*

public fun Application.configureSession(config: AuthProvidersConfig?, block: (SessionsConfig.() -> Unit)? = null) {
    val configBlock: (SessionsConfig.() -> Unit)? = config?.let {
        {

        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(Sessions) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
