package ai.tech.core.misc.plugin.callid

import ai.tech.core.misc.plugin.callid.model.config.CallIdConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*

public fun Application.configureCallId(config: CallIdConfig?) = config?.takeIf { it.enable != false }?.let {
    install(CallId) {
        it.verify?.let { verify(it.dictionary, it.reject) }

        if (it.header?.let { header(it) } == null) {
            it.retrieveFromHeader?.let { retrieveFromHeader(it) }
            it.replyToHeader?.let { replyToHeader(it) }
        }
    }
}
