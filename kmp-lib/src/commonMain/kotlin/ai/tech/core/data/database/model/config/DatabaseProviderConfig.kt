package ai.tech.core.data.database.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DatabaseProviderConfig(
    val connection: DatabaseConnectionConfig,
    val createTables: List<CreateTableConfig> = emptyList(),
    override val enable: Boolean = true
) : EnabledConfig
