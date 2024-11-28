package ai.tech.core.misc.auth.identity.principal.repository

import ai.tech.core.data.database.exposed.AbstractExposedCRUDRepository
import ai.tech.core.misc.auth.identity.principal.model.PrincipalEntity
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

public class PrincipalExposedCRUDRepository(
    database: Database,
    transactionIsolation: Int? = null,
    table: Table,
    timeZone: TimeZone = TimeZone.UTC
) : AbstractExposedCRUDRepository<PrincipalEntity, Long>(
    PrincipalEntity::class,
    database,
    transactionIsolation,
    table,
    timeZone = timeZone,
)
