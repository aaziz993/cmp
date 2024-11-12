package ai.tech.core.misc.plugin.auth.database.principal

import ai.tech.core.misc.kotysa.KotysaCRUDRepository
import ai.tech.core.misc.plugin.auth.database.principal.model.PrincipalEntity
import ai.tech.core.misc.plugin.auth.database.principal.model.PrincipalTable
import org.ufoss.kotysa.R2dbcSqlClient

public class PrincipalKotysaCRUDRepository(client: R2dbcSqlClient) : KotysaCRUDRepository<PrincipalEntity>(
    PrincipalEntity::class,
    client,
    PrincipalTable,
)
