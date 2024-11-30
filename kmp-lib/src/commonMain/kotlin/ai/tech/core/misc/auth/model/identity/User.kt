package ai.tech.core.misc.auth.model.identity

import ai.tech.core.misc.auth.model.Role
import kotlinx.serialization.Serializable

@Serializable
public data class User(
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val image: String? = null,
    val roles: Set<String>? = null,
    val attributes: Map<String, List<String>>? = null
) {

    public fun validate(role: Role? = null): Boolean = role?.validate(roles.orEmpty()) != false
}
