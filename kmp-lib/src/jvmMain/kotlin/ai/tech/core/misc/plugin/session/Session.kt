package ai.tech.core.misc.plugin.session

import ai.tech.core.misc.plugin.auth.model.config.AuthConfig
import io.ktor.server.application.*
import io.ktor.server.sessions.*

public fun Application.configureSession(config: AuthConfig?, block: (SessionsConfig.() -> Unit)? = null) {
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
