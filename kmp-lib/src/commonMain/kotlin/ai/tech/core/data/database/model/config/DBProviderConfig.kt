package ai.tech.core.data.database.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DBProviderConfig(
    val connection: DBConnectionConfig,
    val table: List<DBTableConfig> = emptyList(),
    override val enable: Boolean = true
) : EnabledConfig
