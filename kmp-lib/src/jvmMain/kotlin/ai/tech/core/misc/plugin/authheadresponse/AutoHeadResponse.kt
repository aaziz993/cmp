package ai.tech.core.misc.plugin.authheadresponse

import ai.tech.core.misc.plugin.authheadresponse.model.config.AutoHeadResponseConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*

public fun Application.configureAutoHeadResponse(config: AutoHeadResponseConfig?) = config?.takeIf { it.enable != false }?.let {
    install(AutoHeadResponse) {

    }
}