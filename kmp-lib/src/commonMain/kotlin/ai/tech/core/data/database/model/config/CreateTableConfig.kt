package ai.tech.core.data.database.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class CreateTableConfig(
    val packages: Set<String>,
    val names: Set<String> = emptySet(),
    val inclusive: Boolean = false,
    val ifNotExists: Boolean = true,
)
