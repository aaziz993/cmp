package ai.tech.core.misc.auth.model

import kotlinx.serialization.Serializable

@Serializable
public data class Role(
    val roles: Set<String>,
    val resolution: RoleResolution = RoleResolution.ANY
) {
    public fun validate(roles: Set<String>): Boolean = when (resolution) {
        RoleResolution.ALL -> (roles - this.roles).isEmpty()
        RoleResolution.ANY -> roles.any { it in this.roles }
        RoleResolution.NONE -> roles.none { it in this.roles }
    }
}
