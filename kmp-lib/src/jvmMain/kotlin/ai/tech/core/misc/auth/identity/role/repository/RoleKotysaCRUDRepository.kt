package ai.tech.core.misc.auth.identity.role.repository

import ai.tech.core.data.database.kotysa.AbstractKotysaCRUDRepository
import ai.tech.core.misc.auth.identity.role.model.RoleEntity
import ai.tech.core.misc.auth.identity.role.model.RoleKotysaTable
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.R2dbcSqlClient

public class RoleKotysaCRUDRepository(client: R2dbcSqlClient, timeZone: TimeZone = TimeZone.Companion.UTC) : AbstractKotysaCRUDRepository<RoleEntity>(
    RoleEntity::class,
    client,
    RoleKotysaTable,
    timeZone = timeZone,
)
