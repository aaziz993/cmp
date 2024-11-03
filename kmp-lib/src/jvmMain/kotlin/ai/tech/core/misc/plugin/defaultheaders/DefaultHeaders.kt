package ai.tech.core.misc.plugin.defaultheaders

import ai.tech.core.misc.plugin.defaultheaders.model.config.DefaultHeadersConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

public fun Application.configureDefaultHeaders(config: DefaultHeadersConfig?) = config?.takeIf { it.enable != false }?.let {
    install(DefaultHeaders) {
        it.headers?.forEach {
            header(it.key, it.value)
        }
    }
}
