package ai.tech.core.misc.plugin.auth.database.kotysa.role.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class RoleEntity(
    val id: Long? = null,
    val name: String,
    val userId: Long,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)
