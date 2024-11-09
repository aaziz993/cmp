package ai.tech.core.misc.plugin.auth.model

import kotlinx.serialization.Serializable

@Serializable
public data class PrincipalEntity(
    val id: Long? = null,
    val username: String,
    val password: String,
)
