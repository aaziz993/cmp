package ai.tech.core.data.database.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class DatabaseProviderConfig(
    val connection: DatabaseConnectionConfig,
    val createTables: List<CreateDatabaseTableConfig> = emptyList(),
)
