package ai.tech.core.misc.auth.client.model.config

import ai.tech.core.misc.auth.model.oauth.OAuthClientConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class ClientAuthConfig(
    val keycloak: Map<String, OAuthClientConfig> = emptyMap(),
    val github: Map<String, OAuthClientConfig> = emptyMap(),
    val google: Map<String, OAuthClientConfig> = emptyMap(),
    val facebook: Map<String, OAuthClientConfig> = emptyMap(),
) {
    @Transient
    val oauth: Map<String, OAuthClientConfig> = keycloak + github + github + facebook
}
