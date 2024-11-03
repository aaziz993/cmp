package ai.tech.core.misc.auth.model.identity

import kotlinx.serialization.Serializable

@Serializable
public data class RoleAuth(
    val roles: Set<String>,
    val type: RoleAuthType = RoleAuthType.ANY
) {
    public fun validate(roles: Set<String>): Boolean = when (type) {
        RoleAuthType.ALL -> (roles - this.roles).isEmpty()
        RoleAuthType.ANY -> roles.any { it in this.roles }
        RoleAuthType.NONE -> roles.none { it in this.roles }
    }
}

public fun roles(
    vararg roles: String,
    type: RoleAuthType = RoleAuthType.ALL
): RoleAuth = RoleAuth(roles.toSet(), type)
