package ai.tech.core.misc.plugin.auth.model

import kotlinx.serialization.Serializable

@Serializable
public data class RoleEntity(
    val id: Long? = null,
    val name: String,
    val userId: Long,
)
