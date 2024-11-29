package ai.tech.core.misc.auth.identity.role.repository

import ai.tech.core.data.database.exposed.AbstractExposedCRUDRepository
import ai.tech.core.misc.auth.identity.role.model.RoleEntity
import ai.tech.core.misc.auth.identity.role.model.RoleExposedTable
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

public class RoleExposedCRUDRepository(
    database: Database,
    transactionIsolation: Int? = null,
    timeZone: TimeZone = TimeZone.UTC
) : AbstractExposedCRUDRepository<RoleEntity>(
    RoleEntity::class,
    database,
    transactionIsolation,
    RoleExposedTable,
    timeZone = timeZone,
)
