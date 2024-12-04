package ai.tech.core.misc.auth.model.identity.principal.repository

import ai.tech.core.data.database.exposed.ExposedCRUDRepository
import ai.tech.core.data.transaction.model.TransactionIsolation
import ai.tech.core.misc.auth.model.identity.principal.PrincipalEntity
import ai.tech.core.misc.auth.model.identity.principal.model.PrincipalExposedTable
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database

public class PrincipalExposedCRUDRepository(
    database: Database,
    timeZone: TimeZone = TimeZone.UTC,
    transactionIsolation: TransactionIsolation? = null
) : ExposedCRUDRepository<PrincipalEntity>(
    PrincipalEntity::class,
    database,
    PrincipalExposedTable,
    timeZone = timeZone,
    transactionIsolation = transactionIsolation,
)

public fun <T : Any> Database.principal(
    timeZone: TimeZone = TimeZone.UTC,
    transactionIsolation: TransactionIsolation? = null
): PrincipalExposedCRUDRepository = PrincipalExposedCRUDRepository(
    this,
    timeZone,
    transactionIsolation,
)
