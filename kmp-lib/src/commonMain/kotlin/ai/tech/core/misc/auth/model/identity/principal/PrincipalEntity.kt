package ai.tech.core.misc.auth.model.identity.principal

import ai.tech.core.data.crud.model.entity.EntityWithMetadata
import ai.tech.core.misc.model.NullableJsonValue
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
    override val createdBy: String? = null,
    override val createdAt: LocalDateTime? = null,
    override val updatedBy: String? = null,
    override val updatedAt: LocalDateTime? = null,
) : EntityWithMetadata<Long, LocalDateTime, LocalDateTime> {

    var attributesAsText: String? by NullableJsonValue(attributes)
}
