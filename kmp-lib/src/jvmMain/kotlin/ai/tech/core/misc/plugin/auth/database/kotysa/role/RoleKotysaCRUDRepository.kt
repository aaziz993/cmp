package ai.tech.core.misc.plugin.auth.database.kotysa.role

import ai.tech.core.data.database.kotysa.AbstractKotysaCRUDRepository
import ai.tech.core.misc.plugin.auth.database.kotysa.role.model.RoleEntity
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.Table

public class RoleKotysaCRUDRepository(client: R2dbcSqlClient, table: Table<RoleEntity>, timeZone: TimeZone = TimeZone.Companion.UTC) : AbstractKotysaCRUDRepository<RoleEntity>(
        RoleEntity::class,
        client,
        table,
        timeZone = timeZone,
)
