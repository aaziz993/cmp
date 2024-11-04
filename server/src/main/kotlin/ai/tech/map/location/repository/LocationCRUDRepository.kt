package ai.tech.map.location.repository

import ai.tech.core.data.database.kotysa.KotysaCRUDRepository
import ai.tech.core.misc.location.model.LocationEntity
import ai.tech.map.location.model.LocationTable
import org.ufoss.kotysa.R2dbcSqlClient

public class LocationCRUDRepository(client: R2dbcSqlClient) : KotysaCRUDRepository<LocationEntity>(
    LocationEntity::class,
    client,
    LocationTable
)
