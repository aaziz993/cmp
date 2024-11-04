package ai.tech.core.misc.model.config.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class KtorServerDeploymentConfig(
    val host: String,
    val port: Int?,
    val sslPort: Int? = null,
) {

    @Transient
    val httpURL: String = sslPort?.let { "https://$host:$it" } ?: "http://$host:$port"

    @Transient
    val wsURL: String = sslPort?.let { "wss://$host:$it" } ?: "ws://$host:$port"
}
