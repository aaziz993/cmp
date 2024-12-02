package ai.tech.core.misc.location.repository;

import ai.tech.core.data.database.exposed.AbstractExposedCRUDRepository
import ai.tech.core.data.transaction.model.TransactionIsolation
import ai.tech.core.misc.location.model.LocationEntity
import ai.tech.core.misc.location.model.LocationExposedTable
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database

public class LocationExposedCRUDRepository(
    database: Database,
    timeZone: TimeZone = TimeZone.UTC,
    transactionIsolation: TransactionIsolation? = null
) : AbstractExposedCRUDRepository<LocationEntity>(
    LocationEntity::class,
    database,
    LocationExposedTable,
    timeZone = timeZone,
    transactionIsolation = transactionIsolation,
)
