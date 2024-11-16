package ai.tech.core.misc.type.multiple.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.transformLatest

// Prevent re-emitting flow from WhileSubscribed(5_000)
public class OnetimeWhileSubscribed private constructor(
    private val stopTimeout: Long,
    private val replayExpiration: Long = Long.MAX_VALUE,
) : SharingStarted {

    private val hasCollected: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        require(stopTimeout >= 0) { "stopTimeout($stopTimeout ms) cannot be negative" }
        require(replayExpiration >= 0) { "replayExpiration($replayExpiration ms) cannot be negative" }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> =
        combine(hasCollected, subscriptionCount) { collected, counts ->
            collected to counts
        }
            .transformLatest { pair ->
                val (collected, count) = pair
                if (count > 0 && !collected) {
                    emit(SharingCommand.START)
                    hasCollected.value = true
                }
                else {
                    delay(stopTimeout)
                    if (replayExpiration > 0) {
                        emit(SharingCommand.STOP)
                        delay(replayExpiration)
                    }
                    emit(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
                }
            }
            .dropWhile {
                it != SharingCommand.START
            } // don't emit any STOP/RESET_BUFFER to start with, only START
            .distinctUntilChanged() // just in case somebody forgets it, don't leak our multiple sending of START
}

@Suppress("FunctionName")
public fun SharingStarted.Companion.OnetimeWhileSubscribed(
    stopTimeoutMillis: Long = 0,
    replayExpirationMillis: Long = Long.MAX_VALUE
): SharingStarted = OnetimeWhileSubscribed(stopTimeoutMillis, replayExpirationMillis)
