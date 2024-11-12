package ai.tech.core.misc.plugin.auth.database.kotysa.role.model

import ai.tech.core.misc.kotysa.KotysaCRUDRepository
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.Table

public class RoleKotysaCRUDRepository(client: R2dbcSqlClient, table: Table<RoleEntity>) : KotysaCRUDRepository<RoleEntity>(
    RoleEntity::class,
    client,
    table,
)
