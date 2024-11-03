package ai.tech.core.misc.auth.server.session.model

import kotlinx.serialization.Serializable

@Serializable
public data class ServerSessionAuthConfig(
    val name: String,
    val sessions: Set<String>,
)
