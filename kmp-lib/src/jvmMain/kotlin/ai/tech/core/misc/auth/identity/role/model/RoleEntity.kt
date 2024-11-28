package ai.tech.core.misc.auth.identity.role.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class RoleEntity(
    val id: Long? = null,
    val name: String,
    val principalId: Long,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)
