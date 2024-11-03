package core.plugin.di.module

import core.io.database.kotysa.createKotysaDatabaseClients
import core.io.database.model.config.DatabaseConfig
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.ufoss.kotysa.R2dbcSqlClient

public fun databaseModule(database: DatabaseConfig?): Module = module {
    database?.let {
        createKotysaDatabaseClients(it).forEach { (name, client) ->
            single<R2dbcSqlClient>(named(name)) { client }
        }
    }
}