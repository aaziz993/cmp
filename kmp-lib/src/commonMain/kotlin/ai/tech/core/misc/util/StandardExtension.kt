package ai.tech.core.misc.util

import ai.tech.core.misc.util.model.Retry
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.coroutines.delay

public suspend inline fun <R> run(retry: Retry? = null, failureBlock: (Exception, attempt: Int) -> Unit = { _, _ -> }, block: suspend () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (retry == null) {
        return block()
    }

    with(retry) {
        var currentInterval = interval

        for (attempt in 1..maxAttempts) {
            try {
                return block()
            }
            catch (e: Exception) {
                failureBlock(e, attempt)

                if (attempt < maxAttempts) {
                    delay(currentInterval)

                    currentInterval = currentInterval * multiplier
                }
            }
        }
    }

    throw IllegalStateException("Max retry attempts reached")
}
