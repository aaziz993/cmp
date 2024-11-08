package ai.tech.core.misc.plugin.xhttpmethodoverride

import ai.tech.core.misc.plugin.xhttpmethodoverride.model.config.XHttpMethodOverrideConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.methodoverride.*

public fun Application.configureXHttpMethodOverride(config: XHttpMethodOverrideConfig?, block: (io.ktor.server.plugins.methodoverride.XHttpMethodOverrideConfig.() -> Unit)? = null) {

    val configBlock: (io.ktor.server.plugins.methodoverride.XHttpMethodOverrideConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enable)?.let {
        {
            config.headerName?.let { headerName = it }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(XHttpMethodOverride) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
