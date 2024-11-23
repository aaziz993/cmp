package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.model.config.LogConfig
import kotlinx.serialization.Serializable

@Serializable
public data class HttpClientConfig(
    val log: LogConfig? = null,
    val timeout: TimeoutConfig? = null,
    val cache: CacheConfig? = null
)
