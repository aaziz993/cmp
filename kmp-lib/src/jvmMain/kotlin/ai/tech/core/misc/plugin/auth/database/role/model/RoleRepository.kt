package ai.tech.core.misc.plugin.auth.database.role.model

import ai.tech.core.misc.kotysa.KotysaCRUDRepository
import org.ufoss.kotysa.R2dbcSqlClient

public class RoleKotysaCRUDRepository(client: R2dbcSqlClient) : KotysaCRUDRepository<RoleEntity>(
    RoleEntity::class,
    client,
    RoleTable,
)
