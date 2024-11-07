package ai.tech.core.misc.auth.client.model.config

import ai.tech.core.misc.auth.client.model.config.oauth.ClientOAuthConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ClientAuthConfig(
    val provider: String,
    val oauth: Map<String, ClientOAuthConfig> = emptyMap(),
)
