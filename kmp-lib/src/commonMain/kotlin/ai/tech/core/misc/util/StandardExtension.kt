package ai.tech.core.misc.util

import ai.tech.core.misc.util.model.Retry
import arrow.resilience.Schedule
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.coroutines.delay
import arrow.fx.coroutines.*
import arrow.resilience.retry
import arrow.resilience.retryRaise

public suspend  fun <R> run(
    maxAttempts: Int = 1,
    failureBlock: (Exception, attempt: Int) -> Unit = { _, _ -> },
    block: suspend () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }


    return (Schedule.recurs(maxAttempts) and Schedule.exponential<Unit>(interval, multiplier)).retry {

        block()
    }
}
