package ai.tech.core.misc.auth.identity.role.model

import ai.tech.core.data.crud.model.entity.EntityWithMetadata
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class RoleEntity(
    override val id: Long? = null,
    val name: String,
    val principalId: Long,
    override val createdBy: String? = null,
    override val createdAt: LocalDateTime? = null,
    override val updatedBy: String? = null,
    override val updatedAt: LocalDateTime? = null,
) : EntityWithMetadata<Long, LocalDateTime, LocalDateTime>
