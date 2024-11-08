package ai.tech.core.misc.plugin.defaultheaders

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.defaultheaders.model.config.DefaultHeadersConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

public fun Application.configureDefaultHeaders(config: DefaultHeadersConfig?, block: (io.ktor.server.plugins.defaultheaders.DefaultHeadersConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.defaultheaders.DefaultHeadersConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enable)?.let {
        {
            it.headers?.forEach {
                header(it.key, it.value)
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(DefaultHeaders) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
