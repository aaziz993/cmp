package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.model.config.LogConfig
import io.ktor.client.plugins.logging.LoggingConfig
import kotlinx.serialization.Serializable

@Serializable
public data class KtorClientConfig(
    val log: LogConfig? = null,
    val requestTimeoutMillis: Long? = null,
    val connectTimeoutMillis: Long? = null,
    val socketTimeoutMillis: Long? = null
)
