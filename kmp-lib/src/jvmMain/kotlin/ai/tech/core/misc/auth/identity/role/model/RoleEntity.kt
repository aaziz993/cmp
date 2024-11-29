package ai.tech.core.misc.auth.identity.role.model

import ai.tech.core.data.crud.model.Entity
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class RoleEntity(
    override val id: Long? = null,
    val name: String,
    val principalId: Long,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
) : Entity<Long>
