package ai.tech.core.data.database.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DatabaseProviderConfig(
    val connection: DatabaseConnectionConfig,
    val table: List<TableConfig> = emptyList(),
    override val enable: Boolean = true
) : EnabledConfig
