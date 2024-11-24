package ai.tech.core.misc.plugin.koin.module

import ai.tech.core.data.database.model.config.DBConfig
import ai.tech.core.data.database.model.config.hikariDataSource
import ai.tech.core.misc.kotysa.createKotysaR2dbcSqlClient
import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.ufoss.kotysa.R2dbcSqlClient

public fun databaseModule(config: Map<String?, DBConfig>?): Module = module {
    // Register database clients in Koin
    config?.let {
        val (jdbc, r2dbc) = it.filterValues(EnabledConfig::enabled).entries.partition { (_, config) -> config.protocol == "jdbc" }

        // JDBC Exposed with Hikary
        jdbc.forEach { (name, config) ->
            single<Database>(name?.let { named(it) }) {
                Database.connect(config.hikariDataSource)
            }
        }

        // R2DBC Kotysa
        r2dbc.forEach { (name, config) ->
            single<R2dbcSqlClient>(name?.let { named(it) }) {
                runBlocking {
                    createKotysaR2dbcSqlClient(config)
                }
            }
        }
    }
}
