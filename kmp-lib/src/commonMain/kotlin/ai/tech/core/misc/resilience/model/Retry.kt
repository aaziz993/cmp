package ai.tech.core.misc.resilience.model

import arrow.resilience.Schedule
import arrow.resilience.retry
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable

@Serializable
public data class Retry(
    val interval: Duration = 1.seconds,
    val multiplier: Double = 5.0,
    val maxAttempts: Long = 3,
) {

    public suspend fun <T> run(block: suspend () -> T): T =
        (Schedule.recurs<Throwable>(maxAttempts) and Schedule.exponential<Throwable>(interval, multiplier)).retry(block)
}
