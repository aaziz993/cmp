package ai.tech.core.misc.config.database

import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DatabaseConfig(
    val h2: Map<String, DatabaseProviderConfig> = emptyMap(),
    val sqlite: Map<String, DatabaseProviderConfig> = emptyMap(),
    val postgresql: Map<String, DatabaseProviderConfig> = emptyMap(),
    val mysql: Map<String, DatabaseProviderConfig> = emptyMap(),
    val mssql: Map<String, DatabaseProviderConfig> = emptyMap(),
    val mariadb: Map<String, DatabaseProviderConfig> = emptyMap(),
    val oracle: Map<String, DatabaseProviderConfig> = emptyMap(),
)
