@file:OptIn(ExperimentalForInheritanceCoroutinesApi::class)

package ai.tech.core.presentation.viewmodel

import ai.tech.core.misc.type.multiple.model.OnetimeWhileSubscribed
import ai.tech.core.misc.type.multiple.model.RestartableStateFlow
import ai.tech.core.misc.type.multiple.model.restartableStateIn
import ai.tech.core.presentation.viewmodel.model.RestartableMutableStateFlow
import ai.tech.core.presentation.viewmodel.model.exception.ViewModelStateException
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

// The reasoning 5_000 was chosen for the stopTimeoutMillis can be found in the official Android documentation, which discusses the ANR (Application Not Responding) timeout threshold.
public abstract class AbstractViewModel<A : Any> : ViewModel(), KoinComponent {

    public abstract val savedStateHandle: SavedStateHandle

    public val state: StateFlow<ViewModelState<Int>> = flow { emit(success(0)) }.viewModelStateFlow(success(1))

    private val state1: StateFlow<ViewModelState<Int>>
        field = MutableStateFlow(success(0)).viewModelStateFlow()

    protected open fun exceptionTransform(exception: Throwable): ViewModelStateException = ViewModelStateException(exception)

    public abstract fun action(action: A): Boolean

    protected fun <T : Any> Flow<T>.viewModelStateFlow(
        initialValue: T,
        started: SharingStarted = SharingStarted.OnetimeWhileSubscribed(5_000),
    ): RestartableStateFlow<T> = restartableStateIn(
        started,
        viewModelScope,
        initialValue,
    )

    protected fun <T : Any> MutableStateFlow<T>.viewModelStateFlow(
        onStartUpdate: (suspend MutableStateFlow<T>.(T) -> T)? = null,
        started: SharingStarted = OnetimeWhileSubscribed(5_000)
    ): RestartableMutableStateFlow<T> {

        val restartableStateFlow = if (onStartUpdate == null) {
            this
        }
        else {
            onStart { update { onStartUpdate(it) } }
        }.viewModelStateFlow(value, started)

        return object : RestartableMutableStateFlow<T> by restartableStateFlow {
            override fun restart() = restartableStateFlow.restart()
        }
    }
}
