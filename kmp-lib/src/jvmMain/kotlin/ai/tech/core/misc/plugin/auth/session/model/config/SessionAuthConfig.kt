package ai.tech.core.misc.plugin.auth.session.model.config

import ai.tech.core.misc.plugin.auth.model.config.ChallengeAuthProviderConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SessionAuthConfig(
    val name: String,
    val sessions: Set<String>,
    override val exception: Boolean = false
) : ChallengeAuthProviderConfig
