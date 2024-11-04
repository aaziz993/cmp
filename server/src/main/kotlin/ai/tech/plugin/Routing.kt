package ai.tech.plugin

import ai.tech.core.misc.model.config.server.ServerConfig
import io.ktor.server.routing.*
import ai.tech.map.mapRouting

public fun Routing.routing(config: ServerConfig) {
    mapRouting(config)
}
