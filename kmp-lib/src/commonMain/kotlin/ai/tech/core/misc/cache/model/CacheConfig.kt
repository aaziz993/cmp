package ai.tech.core.misc.cache.model

import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
public data class CacheConfig(
    val size: Long? = null,
    val expiresIn: Duration? = null,
    val expiration: CacheExpiration = CacheExpiration.AFTER_WRITE,
)
