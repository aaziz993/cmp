package ai.tech.core.misc.auth.model

import ai.tech.core.misc.auth.model.identity.User
import kotlinx.serialization.Serializable

@Serializable
public data class AuthResource(
    val providers: List<String?> = listOf(null),
    val role: Role? = null
) {

    public fun validate(user: User?): Boolean = user?.validate(role) == true
}
