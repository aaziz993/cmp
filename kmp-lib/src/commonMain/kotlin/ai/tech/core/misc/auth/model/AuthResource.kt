package ai.tech.core.misc.auth.model

import kotlinx.serialization.Serializable

@Serializable
public data class AuthResource(
    val providers: List<String?> = listOf(null),
    val role: AuthRole? = null
) {

    public fun validate(user: User?): Boolean = user?.validate(role) == true
}
