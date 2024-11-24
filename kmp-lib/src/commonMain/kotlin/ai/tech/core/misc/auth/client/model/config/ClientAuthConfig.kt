package ai.tech.core.misc.auth.client.model.config

import ai.tech.core.misc.auth.client.model.config.oauth.ClientOAuthConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class ClientAuthConfig(
    val providerName: String? = null,
    val oauth: Map<String?, ClientOAuthConfig> = emptyMap(),
) {

    @Transient
    public val providerConfig: ClientOAuthConfig = oauth[providerName]!!
}
