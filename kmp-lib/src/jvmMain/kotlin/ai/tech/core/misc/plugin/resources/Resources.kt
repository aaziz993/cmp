package ai.tech.core.misc.plugin.resources

import ai.tech.core.misc.plugin.resources.model.config.ResourcesConfig
import io.ktor.server.application.*
import io.ktor.server.resources.*

public fun Application.configureResources(config: ResourcesConfig?) =
    config?.takeIf { it.enable != false }?.let {
        install(Resources) {

        }
    }

