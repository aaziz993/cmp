package ai.tech.core.misc.plugin.authheadresponse

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.authheadresponse.model.config.AutoHeadResponseConfig
import arrow.fx.coroutines.onCancel
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*

public fun Application.configureAutoHeadResponse(config: AutoHeadResponseConfig?) = config?.takeIf(EnabledConfig::enable)?.let {
    install(AutoHeadResponse)
}
