@file:OptIn(ExperimentalForInheritanceCoroutinesApi::class)

package ai.tech.core.presentation.viewmodel

import ai.tech.core.misc.type.multiple.model.OnetimeWhileSubscribed
import ai.tech.core.misc.type.multiple.model.RestartableStateFlow
import ai.tech.core.misc.type.multiple.model.restartableStateIn
import ai.tech.core.presentation.viewmodel.ViewModelState.Failure
import ai.tech.core.presentation.viewmodel.ViewModelState.Loading
import ai.tech.core.presentation.viewmodel.ViewModelState.Success
import ai.tech.core.presentation.viewmodel.model.RestartableMutableStateFlow
import ai.tech.core.presentation.viewmodel.model.exception.ViewModelStateException
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
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

    public val state: StateFlow<ViewModelState<Int>> = viewModelStateFlow(success(1)) { emit(success(0)) }

    private val state1: StateFlow<ViewModelState<Int>>
        field = viewModelStateFlow(success(1))

    public open fun exceptionTransform(exception: Throwable): ViewModelStateException = ViewModelStateException(exception)

    public suspend fun <T : Any> map(
        block: suspend () -> T
    ): ViewModelState<T> = null

    public abstract fun action(action: A): Boolean

    protected fun <T : ViewModelState<*>> viewModelStateFlow(
        initialValue: T,
        started: SharingStarted = SharingStarted.OnetimeWhileSubscribed(5_000),
        block: suspend FlowCollector<T>.() -> Unit
    ): RestartableStateFlow<T> = flow(block).viewModelStateFlow(initialValue, started)

    protected fun <T : ViewModelState<*>> viewModelStateFlow(
        initialValue: T,
        onStartUpdate: (suspend MutableStateFlow<T>.(T) -> T)? = null,
        started: SharingStarted = OnetimeWhileSubscribed(STATE_STARTED_STOP_TIMEOUT_MILLIS)
    ): RestartableMutableStateFlow<T> {
        val mutableStateFlow = MutableStateFlow(initialValue)

        val restartableStateFlow = if (onStartUpdate == null) {
            mutableStateFlow
        }
        else {
            mutableStateFlow.onStart { mutableStateFlow.update { mutableStateFlow.onStartUpdate(it) } }
        }.viewModelStateFlow(initialValue, started)

        return object : RestartableMutableStateFlow<T> by restartableStateFlow {
            override fun restart() = restartableStateFlow.restart()
        }
    }

    private fun <T : ViewModelState<*>> Flow<T>.viewModelStateFlow(
        initialValue: T,
        started: SharingStarted = SharingStarted.OnetimeWhileSubscribed(STATE_STARTED_STOP_TIMEOUT_MILLIS),
    ): RestartableStateFlow<T> = restartableStateIn(
        started,
        viewModelScope,
        initialValue,
    )

    public companion object Constants {

        public const val STATE_STARTED_STOP_TIMEOUT_MILLIS: Long = 5_000
    }
}
