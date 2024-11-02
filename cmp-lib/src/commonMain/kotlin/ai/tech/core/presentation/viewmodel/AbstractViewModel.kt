package ai.tech.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent

public abstract class AbstractViewModel<S : Any, A : Any> : ViewModel(), KoinComponent {
    public val state: StateFlow<ViewState<S>>
        field = MutableStateFlow(ViewState.Uninitialized)

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
            } catch (throwable: Throwable) {
                failure(state.data!!, handleThrowable(throwable).also {
                    Logger.e(throwable) { it.message.orEmpty() }
                })
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
