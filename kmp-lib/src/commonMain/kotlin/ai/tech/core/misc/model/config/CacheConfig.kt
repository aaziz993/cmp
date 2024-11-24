package ai.tech.core.misc.model.config

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
public data class CacheConfig(
    val name: String,
    val maximumCacheSize: Long? = null,
    val expireAfterAccess: Duration? = null,
    val expireAfterWrite: Duration? = null,
    override val enabled: Boolean = false,
) : EnabledConfig
