package ai.tech.core.misc.plugin.session.model

import kotlinx.serialization.Serializable

@Serializable
public data class UserSession(
    val username: String,
    val roles: Set<String> = emptySet(),
    val count: Long,
)
