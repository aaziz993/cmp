package ai.tech.core.misc.location.repository;

import ai.tech.core.data.database.exposed.AbstractExposedCRUDRepository
import ai.tech.core.misc.location.model.LocationEntity
import ai.tech.core.misc.location.model.LocationExposedTable
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database

import org.jetbrains.exposed.sql.Table;

public class LocationExposedCRUDRepository(
    database: Database,
    transactionIsolation: Int? = null,
    timeZone: TimeZone = TimeZone.UTC
) : AbstractExposedCRUDRepository<LocationEntity>(
    LocationEntity::class,
    database,
    transactionIsolation,
    LocationExposedTable,
    timeZone = timeZone,
)
