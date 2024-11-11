package ai.tech.core.misc.location.repository

import ai.tech.core.misc.kotysa.KotysaCRUDRepository
import ai.tech.core.misc.location.model.LocationEntity
import ai.tech.core.misc.location.model.LocationTable
import org.ufoss.kotysa.R2dbcSqlClient

public class LocationKotysaCRUDRepository(client: R2dbcSqlClient) : KotysaCRUDRepository<LocationEntity>(
    LocationEntity::class,
    client,
    LocationTable
)
