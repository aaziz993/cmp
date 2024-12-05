package ai.tech.core.misc.type.multiple

import io.ktor.util.cio.ChannelWriteException
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

public fun MutableSharedFlow<*>.onHasSubscriptionChange(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    change: (Boolean) -> Unit,
): Job =
    subscriptionCount
        .map { count -> count > 0 }
        .distinctUntilChanged()
        .onEach(change)
        .launchIn(coroutineScope)

public suspend fun Flow<String>.toByteWriteChannel(byteWriteChannel: ByteWriteChannel) {
    val job = CoroutineScope(currentCoroutineContext()).launch {
        collect { value ->
            try {
                byteWriteChannel.writeStringUtf8("$value\n")
                byteWriteChannel.flush()
            }
            catch (e: ChannelWriteException) {
                byteWriteChannel.cancel(e)
            }
        }
    }

    job.join()
}
