package ai.tech.core.misc.plugin.ratelimit.model.config

import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
public data class RateLimitConfig(
    val limit: Int,
    val refillPeriod: Duration,
    val initialSize: Int = limit,
)
