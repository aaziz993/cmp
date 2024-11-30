package ai.tech.core.data.database.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class TableConfig(
    val tables: Set<String> = emptySet(),
    val scanPackage: String,
    val excludePatterns: List<String> = emptyList(),
    val create: Creation = Creation.IF_NOT_EXISTS,
    // Only Exposed
    val createInBatch: Boolean = false,
    override val enabled: Boolean = true
) : EnabledConfig
