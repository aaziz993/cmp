package ai.tech.core.data.store5.model

import ai.tech.core.data.store5.Operation
import kotlinx.serialization.Serializable

@Serializable
public data class BookKeeperEntity(
    val id: Long? = null,
    val operationId: String,
    // Timestamp of the last failed sync attempt for the given key.
    val timestamp: Long,
)
