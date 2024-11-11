@file:OptIn(ExperimentalForInheritanceCoroutinesApi::class)

package ai.tech.core.presentation.viewmodel

import ai.tech.core.misc.type.multiple.model.OnetimeWhileSubscribed
import ai.tech.core.misc.type.multiple.model.RestartableStateFlow
import ai.tech.core.misc.type.multiple.restartableStateIn
import ai.tech.core.presentation.viewmodel.ViewModelState.Loading
import ai.tech.core.presentation.viewmodel.ViewModelState.Success
import ai.tech.core.presentation.viewmodel.model.ViewModelMutableStateFlow
import ai.tech.core.presentation.viewmodel.model.exception.ViewModelStateException
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

public abstract class AbstractViewModel<A : Any> : ViewModel(), KoinComponent {

    public abstract val savedStateHandle: SavedStateHandle

    public open fun exceptionTransform(exception: Throwable): ViewModelStateException = ViewModelStateException(exception)

    public suspend fun <T : Any> ViewModelState<T>.map(
        block: suspend () -> T): ViewModelState<T> = try {
        Loading(block())
    }
    catch (e: Throwable) {
        toFailure(exceptionTransform(e))
    }

    public suspend fun <T : Any> ViewModelState<T>.mapResult(
        block: suspend () -> Result<T>): ViewModelState<T> = block().fold(
        onSuccess = { Success(it) },
        onFailure = { toFailure(exceptionTransform(it)) },
    )

    public suspend fun <T : Any> ViewModelState<T>.mapEither(
        block: suspend () -> Either<Throwable, T>
    ): ViewModelState<T> = block().fold(
        ifLeft = { toFailure(exceptionTransform(it)) },
        ifRight = { Success(it) },
    )

    public suspend fun <T : Any> ViewModelState<T>.mapRaise(
        block: suspend Raise<Throwable>.() -> T
    ): ViewModelState<T> = mapEither { either<Throwable, T> { block() } }

    public abstract fun action(action: A): Boolean

    protected fun <T : Any> viewModelStateFlow(
        initialValue: ViewModelState<T> = idle(),
        started: SharingStarted = SharingStarted.OnetimeWhileSubscribed(5_000),
        block: suspend FlowCollector<ViewModelState<T>>.(ViewModelState<T>) -> Unit
    ): RestartableStateFlow<ViewModelState<T>> = flow { block(initialValue) }.viewModelStateFlow(initialValue, started)

    protected fun <T : Any> viewModelStateFlow(
        initialValue: ViewModelState<T> = idle(),
        onStartUpdate: (suspend MutableStateFlow<ViewModelState<T>>.(ViewModelState<T>) -> ViewModelState<T>)? = null,
        started: SharingStarted = OnetimeWhileSubscribed(STATE_STARTED_STOP_TIMEOUT_MILLIS)
    ): ViewModelMutableStateFlow<T> {
        val mutableStateFlow = MutableStateFlow(initialValue)

        val restartableStateFlow = if (onStartUpdate == null) {
            mutableStateFlow
        }
        else {
            mutableStateFlow.onStart { mutableStateFlow.update { mutableStateFlow.onStartUpdate(it) } }
        }.viewModelStateFlow(initialValue, started)

        return object : ViewModelMutableStateFlow<T> by restartableStateFlow {
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

    public companion object {

        // The reasoning 5_000 was chosen for the stopTimeoutMillis can be found in the official Android documentation, which discusses the ANR (Application Not Responding) timeout threshold.
        public const val STATE_STARTED_STOP_TIMEOUT_MILLIS: Long = 5_000
    }
}
