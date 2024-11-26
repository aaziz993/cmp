package ai.tech.core.misc.util.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.serialization.Serializable

@Serializable
public data class Retry(
    val maxAttempts: Int = 3,
    val interval: Duration = 1.seconds,
    val multiplier: Double = 1.5,
)
