package ai.tech.core.misc.plugin.auth.database.kotysa.role;

import ai.tech.core.data.database.exposed.AbstractExposedCRUDRepository
import ai.tech.core.misc.plugin.auth.database.kotysa.role.model.RoleEntity
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

public class RoleExposedCRUDRepository(
    database: Database,
    transactionIsolation: Int? = null,
    table: Table,
    timeZone: TimeZone = TimeZone.UTC
) : AbstractExposedCRUDRepository<RoleEntity>(
    RoleEntity::class,
    database,
    transactionIsolation,
    table,
    timeZone = timeZone,
)
