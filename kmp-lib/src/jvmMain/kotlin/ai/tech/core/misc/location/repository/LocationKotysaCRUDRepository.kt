package ai.tech.core.misc.location.repository

import ai.tech.core.data.database.kotysa.AbstractKotysaCRUDRepository
import ai.tech.core.misc.location.model.LocationEntity
import ai.tech.core.misc.location.model.LocationTable
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.R2dbcSqlClient

public class LocationKotysaCRUDRepository(client: R2dbcSqlClient, timeZone: TimeZone = TimeZone.UTC) : AbstractKotysaCRUDRepository<LocationEntity>(
        LocationEntity::class,
        client,
        LocationTable,
        timeZone = timeZone,
)
