package ai.tech.core.misc.auth.identity.principal.model

import ai.tech.core.data.crud.model.Entity
import ai.tech.core.data.database.kotysa.model.JsonValue
import ai.tech.core.data.database.kotysa.model.NullableJsonValue
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class PrincipalEntity(
    override val id: Long? = null,
    val username: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val image: String? = null,
    val roles: Set<String>? = null,
    var attributes: Map<String, List<String>>? = null,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
) : Entity<Long> {

    var attributesAsText: String? by NullableJsonValue(attributes)
}
