package ai.tech.core.misc.plugin.dataconversion

import ai.tech.core.misc.plugin.dataconversion.model.config.DataConversionConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.dataconversion.*

public fun Application.configureDataConversion(config: DataConversionConfig?) = config?.takeIf { it.enable != false }?.let {
    install(DataConversion) {

    }
}
