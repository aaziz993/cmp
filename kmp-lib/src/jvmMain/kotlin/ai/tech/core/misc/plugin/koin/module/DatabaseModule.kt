package ai.tech.core.misc.plugin.koin.module

import ai.tech.core.misc.kotysa.createKotysaR2dbcSqlClient
import ai.tech.core.data.database.model.config.DBProviderConfig
import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.ufoss.kotysa.R2dbcSqlClient

public fun databaseModule(config: Map<String, DBProviderConfig>?): Module = module {
    // Register database clients in Koin
    config?.let {
        val (r2dbc, jdbc) = it.filterValues(EnabledConfig::enable).entries.partition { (_, config) -> config.connection.protocol == "r2dbc" }

        // R2DBC Kotysa
        r2dbc.forEach { (name, config) ->
            single<R2dbcSqlClient>(named(name)) {
                runBlocking {
                    createKotysaR2dbcSqlClient(config)
                }
            }
        }
    }
}
