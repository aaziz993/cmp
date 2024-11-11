@file:OptIn(ExperimentalForInheritanceCoroutinesApi::class)

package ai.tech.core.misc.type.multiple.model

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

// Provide ability to re-fetch data if the response fails or just refresh, allowing the user to retry and improve their overall experience.
public interface RestartableStateFlow<out T> : StateFlow<T> {

    public fun restart()
}
