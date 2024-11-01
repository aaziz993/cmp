package ai.tech.core.misc.auth.model

import kotlinx.serialization.Serializable

@Serializable
public enum class RoleAuthType {
    ALL,
    ANY,
    NONE,
}
