package ai.tech.core.misc.plugin.auth.database.principal.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class PrincipalEntity(
    val id: Long? = null,
    val username: String,
    val password: String,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)
