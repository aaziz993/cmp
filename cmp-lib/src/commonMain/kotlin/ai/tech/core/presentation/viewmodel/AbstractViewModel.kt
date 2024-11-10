package ai.tech.core.presentation.viewmodel

import ai.tech.core.misc.type.multiple.model.OnetimeWhileSubscribed
import ai.tech.core.misc.type.multiple.model.RestartableStateFlow
import ai.tech.core.misc.type.multiple.model.restartableStateIn
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent



public abstract class AbstractViewModel : ViewModel(), KoinComponent {

    public abstract val savedStateHandle: SavedStateHandle

    public val state: RestartableStateFlow<ViewModelState<Int>> = flow {
        emit(loading(1))
    }.viewModelStateFlow()

    // The reasoning 5_000 was chosen for the stopTimeoutMillis can be found in the official Android documentation, which discusses the ANR (Application Not Responding) timeout threshold.
    public fun <T : Any> Flow<ViewModelState<T>>.viewModelStateFlow(
        started: SharingStarted = SharingStarted.OnetimeWhileSubscribed(5_000),
        initialValue: ViewModelState<T> = ViewModelState.Idle,
    ): RestartableStateFlow<ViewModelState<T>> = restartableStateIn(
        started,
        viewModelScope,
        initialValue,
    )

    @OptIn(ExperimentalForInheritanceCoroutinesApi::class)
    public fun <T : Any> MutableStateFlow<ViewModelState<T>>.viewModelStateFlow(
        started: SharingStarted = OnetimeWhileSubscribed(5_000),
        initialValue: ViewModelState<T> = ViewModelState.Idle,
        fetchData: suspend () -> T
    ): RestartableStateFlow<ViewModelState<T>> {
        val mutableStateFlow = MutableStateFlow(initialValue)

        return mutableStateFlow.onStart {
            mutableStateFlow.update { success(fetchData()) }
        }.viewModelStateFlow(started, initialValue)
    }

    //    protected open fun onInitialized(): Unit = Unit
//
//    abstract suspend fun initialStateData(): S
//
//    protected open fun handleThrowable(throwable: Throwable): Throwable = when (throwable) {
//        is HttpResponseException -> {
//            Throwable("network_${throwable.status.value}", throwable)
//        }
//
//        else -> {
//            throwable
//        }
//    }
//
    protected fun update(block: suspend (S) -> S): Job = viewModelScope.launch {
        state.update { state ->
            try {
                success(block(state.data!!))
            }
            catch (throwable: Throwable) {
                failure(
                    state.data!!,
                    handleThrowable(throwable).also {
                        Logger.e(throwable) { it.message.orEmpty() }
                    },
                )
            }
        }
    }
//
//    fun loading() {
//        _state.update { ViewState.Loading(it.data!!) }
//    }
//
//    fun restoreState(): Job? = if (state.value !is ViewState.Success) {
//        update { it }
//    } else {
//        null
//    }
//
//    abstract fun action(action: A)
}
