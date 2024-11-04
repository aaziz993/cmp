package ai.tech.map.location

import ai.tech.core.misc.model.config.server.ServerConfig
import io.ktor.server.routing.*

public fun Routing.locationRouting(config: ServerConfig): Unit = with(config.presentation.screen.map) {
//    crudRouting("$destination/$database/location", LocationCRUDRepository(get(named(database!!))))
}
