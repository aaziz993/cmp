package ai.tech.core.misc.location.repository

import ai.tech.core.misc.kotysa.KotysaCRUDRepository
import ai.tech.core.misc.location.model.LocationEntity
import ai.tech.core.misc.location.model.LocationTable
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.R2dbcSqlClient

public class LocationKotysaCRUDRepository(client: R2dbcSqlClient, timeZone: TimeZone = TimeZone.UTC) : KotysaCRUDRepository<LocationEntity>(
        LocationEntity::class,
        client,
        LocationTable,
        timeZone = timeZone,
)
