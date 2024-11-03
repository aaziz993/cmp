package ai.tech.core.misc.plugin.ratelimit.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class RateLimitsConfig(
    val global: RateLimitConfig? = null,
    val specific: Map<String, RateLimitConfig>? = null,
    override val enable: Boolean? = null,
) : EnabledConfig
