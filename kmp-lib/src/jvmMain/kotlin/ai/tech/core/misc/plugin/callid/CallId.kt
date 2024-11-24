package ai.tech.core.misc.plugin.callid

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.callid.model.config.CallIdConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*

public fun Application.configureCallId(config: CallIdConfig?, block: (io.ktor.server.plugins.callid.CallIdConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.callid.CallIdConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            it.verify?.takeIf(EnabledConfig::enabled)?.let { verify(it.dictionary, it.reject) }

            if (it.header?.let { header(it) } == null) {
                it.retrieveFromHeader?.let { retrieveFromHeader(it) }
                it.replyToHeader?.let { replyToHeader(it) }
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(CallId) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
