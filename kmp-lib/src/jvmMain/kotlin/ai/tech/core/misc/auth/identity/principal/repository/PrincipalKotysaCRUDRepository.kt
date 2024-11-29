package ai.tech.core.misc.auth.identity.principal.repository

import ai.tech.core.data.database.kotysa.AbstractKotysaCRUDRepository
import ai.tech.core.misc.auth.identity.principal.model.PrincipalEntity
import ai.tech.core.misc.auth.identity.principal.model.PrincipalKotysaTable
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.Table

public class PrincipalKotysaCRUDRepository(client: R2dbcSqlClient, timeZone: TimeZone = TimeZone.UTC) : AbstractKotysaCRUDRepository<PrincipalEntity>(
    PrincipalEntity::class,
    client,
    PrincipalKotysaTable,
    timeZone = timeZone,
)
