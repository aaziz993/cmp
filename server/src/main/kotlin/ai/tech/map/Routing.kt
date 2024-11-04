package ai.tech.map

import ai.tech.core.misc.model.config.server.ServerConfig
import io.ktor.server.routing.*
import ai.tech.core.misc.location.http.locationRouting

public fun Routing.mapRouting(config: ServerConfig) {
    locationRouting(config)
}
