package ai.tech.core.misc.auth.server.model.config

import ai.tech.core.misc.auth.server.jwt.model.ServerJWTHS256Config
import ai.tech.core.misc.auth.server.jwt.model.ServerJWTRS256Config
import ai.tech.core.misc.auth.server.oauth.model.config.ServerOAuthConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class ServerAuthConfig(
    val keycloak: Map<String, ServerOAuthConfig> = emptyMap(),
    val github: Map<String, ServerOAuthConfig> = emptyMap(),
    val google: Map<String, ServerOAuthConfig> = emptyMap(),
    val facebook: Map<String, ServerOAuthConfig> = emptyMap(),
    val jwtHs256: Map<String, ServerJWTHS256Config> = emptyMap(),
    val jwtRs256: Map<String, ServerJWTRS256Config> = emptyMap(),
) {

    @Transient
    val oauths: Map<String, ServerOAuthConfig> = keycloak + github + google + facebook
}
