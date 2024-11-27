package ai.tech.core.data.database.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class SchemaConfig(
    val name: String,
    val create: Creation = Creation.IF_NOT_EXISTS,
    val createInBatch:Boolean = false,
)
