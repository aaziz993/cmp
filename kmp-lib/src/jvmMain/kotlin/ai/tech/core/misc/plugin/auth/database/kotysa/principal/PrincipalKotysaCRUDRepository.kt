package ai.tech.core.misc.plugin.auth.database.kotysa.principal

import ai.tech.core.misc.kotysa.KotysaCRUDRepository
import ai.tech.core.misc.plugin.auth.database.kotysa.principal.model.PrincipalEntity
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.Table

public class PrincipalKotysaCRUDRepository(client: R2dbcSqlClient, table: Table<PrincipalEntity>, timeZone: TimeZone = TimeZone.UTC) : KotysaCRUDRepository<PrincipalEntity>(
    PrincipalEntity::class,
    client,
    table,
    timeZone = timeZone,
)
