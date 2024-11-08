package ai.tech.core.misc.plugin.di.module

import ai.tech.core.data.database.kotysa.createKotysaR2dbcClient
import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.ufoss.kotysa.R2dbcSqlClient

public fun databaseModule(config: Map<String, DatabaseProviderConfig>?): Module = module {
    // Register database clients in Koin
    config?.let {
        val (r2dbc, jdbc) = it.entries.filter { (_, v) -> v.enable != false }.partition { (_, v) -> v.connection.protocol == "r2dbc" }

        // R2DBC with Kotysa
        r2dbc.forEach { (k, v) ->
            single<R2dbcSqlClient>(named(k)) { createKotysaR2dbcClient(v) }
        }
    }
}
