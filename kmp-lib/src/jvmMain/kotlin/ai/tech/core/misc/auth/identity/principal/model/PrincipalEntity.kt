package ai.tech.core.misc.auth.identity.principal.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class PrincipalEntity(
    val id: Long? = null,
    val username: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val image: String? = null,
    val roles: Set<String>? = null,
    val attributes: Map<String, List<String>>? = null,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)
