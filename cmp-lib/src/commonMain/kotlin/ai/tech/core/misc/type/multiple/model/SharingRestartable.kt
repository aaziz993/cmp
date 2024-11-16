package ai.tech.core.misc.type.multiple.model

import kotlinx.coroutines.flow.*

public interface SharingRestartable: SharingStarted {
    public fun restart()
}

public fun SharingStarted.makeRestartable(): SharingRestartable {
    return SharingRestartableImpl(this)
}

private data class SharingRestartableImpl(
    private val sharingStarted: SharingStarted,
): SharingRestartable {

    private val restartFlow = MutableSharedFlow<SharingCommand>(extraBufferCapacity = 2)

    override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> {
        return merge(restartFlow, sharingStarted.command(subscriptionCount))
    }

    override fun restart() {
        restartFlow.tryEmit(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
        restartFlow.tryEmit(SharingCommand.START)
    }
}
