@file:OptIn(ExperimentalForInheritanceCoroutinesApi::class)

package ai.tech.core.misc.type.multiple.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// Provide ability to re-fetch data if the response fails or just refresh, allowing the user to retry and improve their overall experience.
public interface RestartableStateFlow<out T> :  StateFlow<T>{
    public fun restart()
}

public fun <T> Flow<T>.restartableStateIn(
    started: SharingStarted,
    scope: CoroutineScope,
    initialValue: T
): RestartableStateFlow<T> {
    val sharingRestartable = started.makeRestartable()
    val stateFlow = stateIn(scope, sharingRestartable, initialValue)
    return object : RestartableStateFlow<T>, StateFlow<T> by stateFlow {
        override fun restart() = sharingRestartable.restart()
    }
}
