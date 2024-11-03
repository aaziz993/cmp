package ai.tech.core.misc.plugin.di.module

import ai.tech.core.data.database.kotysa.createKotysaR2dbcClient
import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.ufoss.kotysa.R2dbcSqlClient

public fun kotysaDatabaseModule(config: Map<String, DatabaseProviderConfig>?): Module = module {
    config?.forEach { (k, v) ->
        single<R2dbcSqlClient>(named(k)) { createKotysaR2dbcClient(v) }
    }
}
