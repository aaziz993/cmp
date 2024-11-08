package ai.tech.core.data.keyvalue.kstore.model;

import kotlinx.serialization.Serializable

@Serializable
public data class KeyValue(
    val key: String,
    val value: String
)
