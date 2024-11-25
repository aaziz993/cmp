package ai.tech.core.misc.plugin.auth.database.kotysa.principal

import ai.tech.core.data.database.exposed.AbstractExposedCRUDRepository
import ai.tech.core.misc.plugin.auth.database.kotysa.principal.model.PrincipalEntity
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

public class PrincipalExposedCRUDRepository(
    database: Database,
    transactionIsolation: Int? = null,
    table: Table,
    timeZone: TimeZone = TimeZone.UTC
) : AbstractExposedCRUDRepository<PrincipalEntity>(
    PrincipalEntity::class,
    database,
    transactionIsolation,
    table,
    timeZone = timeZone,
)
