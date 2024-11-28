package ai.tech.core.misc.auth.identity.role.repository

import ai.tech.core.data.database.kotysa.AbstractKotysaCRUDRepository
import ai.tech.core.misc.auth.identity.role.model.RoleEntity
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.Table

public class RoleKotysaCRUDRepository(client: R2dbcSqlClient, table: Table<RoleEntity>, timeZone: TimeZone = TimeZone.Companion.UTC) : AbstractKotysaCRUDRepository<RoleEntity, Long>(
    RoleEntity::class,
    client,
    table,
    timeZone = timeZone,
)
