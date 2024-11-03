package ai.tech.core.misc.auth.model

import ai.tech.core.misc.auth.model.role.RoleAuth
import kotlinx.serialization.Serializable

@Serializable
public data class AuthResource(
    val authProviders: List<String?> = listOf(null),
    val roleAuth: RoleAuth? = null
) {
    public constructor(authProvider: String?, roleAuth: RoleAuth? = null) : this(listOf(authProvider), roleAuth)

    public fun validate(user: User?): Boolean = user?.validate(roleAuth) == true
}
