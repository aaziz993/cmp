package ai.tech.core.misc.type.multiple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

public fun MutableSharedFlow<*>.onHasSubscriptionChange(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    change: (Boolean) -> Unit,
): Job =
    subscriptionCount
        .map { count -> count > 0 }
        .distinctUntilChanged()
        .onEach(change)
        .launchIn(coroutineScope)
