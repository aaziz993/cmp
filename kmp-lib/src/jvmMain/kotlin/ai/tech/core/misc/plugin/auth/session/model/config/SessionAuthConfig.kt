package ai.tech.core.misc.plugin.auth.session.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class SessionAuthConfig(
    val name: String,
    val sessions: Set<String>,
)
