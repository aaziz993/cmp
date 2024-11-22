package ai.tech.map

import ai.tech.core.data.crud.server.http.CrudRouting
import ai.tech.core.misc.location.repository.LocationKotysaCRUDRepository
import io.ktor.server.routing.*
import ai.tech.core.misc.model.config.ServerConfig
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get

public fun Routing.mapRouting(config: ServerConfig) = with(config.presentation.destination.map) {
    CrudRouting("$route/database/location", LocationKotysaCRUDRepository(get(named( " "))))
}
