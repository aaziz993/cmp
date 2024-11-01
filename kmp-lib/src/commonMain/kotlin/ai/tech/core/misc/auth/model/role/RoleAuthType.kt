package ai.tech.core.misc.auth.model.role

import kotlinx.serialization.Serializable

@Serializable
public enum class RoleAuthType {
    ALL,
    ANY,
    NONE,
}
