package ai.tech.core.misc.plugin.auth.database.kotysa.principal

import ai.tech.core.misc.kotysa.KotysaCRUDRepository
import ai.tech.core.misc.plugin.auth.database.kotysa.principal.model.PrincipalEntity
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.Table

public class PrincipalKotysaCRUDRepository(client: R2dbcSqlClient, table: Table<PrincipalEntity>) : KotysaCRUDRepository<PrincipalEntity>(
    PrincipalEntity::class,
    client,
    table,
)
