package ai.tech.core.misc.location.repository

import ai.tech.core.data.database.kotysa.KotysaCRUDRepository
import ai.tech.core.misc.location.model.GeolocationEntity
import ai.tech.core.misc.location.model.LocationTable
import org.ufoss.kotysa.R2dbcSqlClient

public class LocationKotysaCRUDRepository(client: R2dbcSqlClient) : KotysaCRUDRepository<GeolocationEntity>(
    GeolocationEntity::class,
    client,
    LocationTable
)
