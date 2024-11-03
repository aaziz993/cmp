package ai.tech.core.misc.plugin.xhttpmethodoverride

import ai.tech.core.misc.plugin.xhttpmethodoverride.model.config.XHttpMethodOverrideConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.methodoverride.*

public fun Application.configureXHttpMethodOverride(config: XHttpMethodOverrideConfig?) =
    config?.takeIf { it.enable != false }?.let {
        install(XHttpMethodOverride) {
            config.headerName?.let { headerName = it }
        }
    }
