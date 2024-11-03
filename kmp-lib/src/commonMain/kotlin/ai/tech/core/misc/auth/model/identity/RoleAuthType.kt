package ai.tech.core.misc.auth.model.identity

import kotlinx.serialization.Serializable

@Serializable
public enum class RoleAuthType {
    ALL,
    ANY,
    NONE,
}
