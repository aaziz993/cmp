package ai.tech.core.misc.model.config.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class KtorServerConfig(
    val host: String,
    val port: Int = 8080,
    val ssl: SSLConfig? = null,
) {

    val preferredSslPort: Int = ssl?.port ?: port

    @Transient
    val httpURL: String = ssl?.port?.let { "https://$host:$it" } ?: "http://$host:$port"

    @Transient
    val wsURL: String = ssl?.port?.let { "wss://$host:$it" } ?: "ws://$host:$port"
}
