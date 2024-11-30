package ai.tech.core.misc.auth.model.identity.role.repository

import ai.tech.core.data.database.exposed.AbstractExposedCRUDRepository
import ai.tech.core.data.transaction.model.TransactionIsolation
import ai.tech.core.misc.auth.model.identity.role.RoleEntity
import ai.tech.core.misc.auth.model.identity.role.model.RoleExposedTable
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database

public class RoleExposedCRUDRepository(
    database: Database,
    timeZone: TimeZone = TimeZone.UTC,
    transactionIsolation: TransactionIsolation? = null
) : AbstractExposedCRUDRepository<RoleEntity>(
    RoleEntity::class,
    database,
    RoleExposedTable,
    timeZone = timeZone,
    transactionIsolation = transactionIsolation,
)
