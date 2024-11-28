package ai.tech.core.misc.location.repository;

import ai.tech.core.data.database.exposed.AbstractExposedCRUDRepository
import ai.tech.core.misc.location.model.LocationEntity
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database

import org.jetbrains.exposed.sql.Table;

public class LocationExposedCRUDRepository(
    database: Database,
    transactionIsolation: Int? = null,
    table: Table,
    timeZone: TimeZone = TimeZone.UTC
) : AbstractExposedCRUDRepository<LocationEntity, Long>(
    LocationEntity::class,
    database,
    transactionIsolation,
    table,
    timeZone = timeZone,
)
