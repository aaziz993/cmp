package ai.tech.map

import ai.tech.core.data.crud.server.http.CrudRouting
import ai.tech.core.misc.location.repository.LocationKotysaCRUDRepository
import ai.tech.core.misc.model.config.server.ServerConfigImpl
import io.ktor.server.routing.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get

public fun Routing.mapRouting(config: ServerConfigImpl) = with(config.ui) {
    with(presentation.destination.map) {
        CrudRouting("$database/$route/location", LocationKotysaCRUDRepository(get(named(database))))
    }
}
