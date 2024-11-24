package ai.tech.core.data.cache.redis.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class RedisConfig(
    val host: String? = null,
    val port: Int? = null,
    val username: String? = null,
    val password: String? = null,
    val lockExpirationMs: Long? = null,
    val connectionPoolInitialSize: Int? = null,
    val connectionPoolMaxSize: Int? = null,
    val connectionAcquisitionTimeoutMs: Long? = null,
    override val enable: Boolean = true,
) : EnabledConfig
