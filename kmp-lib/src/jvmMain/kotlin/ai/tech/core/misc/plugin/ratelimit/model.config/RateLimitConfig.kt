package ai.tech.core.misc.plugin.ratelimit.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
public data class RateLimitConfig(
    val limit: Int,
    val refillPeriod: Duration,
    val initialSize: Int = limit,
    override val enabled: Boolean = true,
) : EnabledConfig
