package ai.tech.map

import ai.tech.core.data.crud.server.http.CrudRouting
import ai.tech.core.misc.location.repository.LocationKotysaCRUDRepository
import io.ktor.server.routing.*
import ai.tech.core.misc.model.config.server.ServerConfigImpl
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get

public fun Routing.mapRouting(config: ServerConfigImpl) = with(config.ui) {
    CrudRouting("$route/$database/location", LocationKotysaCRUDRepository(get(named( " "))))
}
