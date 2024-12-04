package ai.tech.core.misc.resilience.model

import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
public data class RateLimitConfig(
    val size: Int,
    val rate: Duration
)
