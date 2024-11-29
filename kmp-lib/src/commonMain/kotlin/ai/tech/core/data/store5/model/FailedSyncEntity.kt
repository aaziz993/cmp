package ai.tech.core.data.store5.model

import ai.tech.core.data.crud.model.entity.EntityWithMetadata
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class FailedSyncEntity(
    override val id: Long? = null,
    val operationId: String,
    // Timestamp of the last failed sync attempt for the given key.
    val timestamp: Long,
    override val createdBy: String? = null,
    override val createdAt: LocalDateTime? = null,
    override val updatedBy: String? = null,
    override val updatedAt: LocalDateTime? = null,
) : EntityWithMetadata<Long, LocalDateTime, LocalDateTime>
