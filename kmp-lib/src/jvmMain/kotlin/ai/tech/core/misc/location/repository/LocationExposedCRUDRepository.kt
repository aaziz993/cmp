package ai.tech.core.misc.location.repository;

import ai.tech.core.data.database.exposed.ExposedCRUDRepository
import ai.tech.core.data.transaction.model.TransactionIsolation
import ai.tech.core.misc.auth.model.identity.principal.repository.PrincipalExposedCRUDRepository
import ai.tech.core.misc.location.model.LocationEntity
import ai.tech.core.misc.location.model.LocationExposedTable
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database

public class LocationExposedCRUDRepository(
    database: Database,
    timeZone: TimeZone = TimeZone.UTC,
    transactionIsolation: TransactionIsolation? = null
) : ExposedCRUDRepository<LocationEntity>(
    LocationEntity::class,
    database,
    LocationExposedTable,
    timeZone = timeZone,
    transactionIsolation = transactionIsolation,
)

public fun <T : Any> Database.principal(
    timeZone: TimeZone = TimeZone.UTC,
    transactionIsolation: TransactionIsolation? = null
): LocationExposedCRUDRepository = LocationExposedCRUDRepository(
    this,
    timeZone,
    transactionIsolation,
)
