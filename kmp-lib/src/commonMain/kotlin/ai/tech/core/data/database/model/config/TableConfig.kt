package ai.tech.core.data.database.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class TableConfig(
    val packages: Set<String>,
    val names: Set<String> = emptySet(),
    val inclusive: Boolean = false,
    val create: TableCreation = TableCreation.IF_NOT_EXISTS,
)
