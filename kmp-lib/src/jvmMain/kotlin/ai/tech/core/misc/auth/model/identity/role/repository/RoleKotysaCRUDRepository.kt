package ai.tech.core.misc.auth.model.identity.role.repository

import ai.tech.core.data.database.kotysa.KotysaCRUDRepository
import ai.tech.core.misc.auth.model.identity.role.RoleEntity
import ai.tech.core.misc.auth.model.identity.role.model.RoleKotysaTable
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.R2dbcSqlClient

public class RoleKotysaCRUDRepository(client: R2dbcSqlClient, timeZone: TimeZone = TimeZone.Companion.UTC) : KotysaCRUDRepository<RoleEntity>(
    RoleEntity::class,
    client,
    RoleKotysaTable,
    timeZone = timeZone,
)
