package ai.tech.core.presentation.viewmodel

import ai.tech.core.presentation.component.loading.LoadingCircle
import ai.tech.core.presentation.viewmodel.model.exception.ViewModelStateException
import androidx.compose.runtime.Composable

public sealed interface ViewModelState<T : Any> {

    public data class Idle<T : Any>(val exception: ViewModelStateException? = null) : ViewModelState<T>

    public data class Loading<T : Any>(val data: T) : ViewModelState<T>

    public data class Success<T : Any>(val data: T) : ViewModelState<T>

    public data class Failure<T : Any>(val data: T, val exception: ViewModelStateException) :
        ViewModelState<T>

    public fun toLoading(): ViewModelState<T> =
        when (this) {
            is Idle -> Idle()
            is Loading -> Loading(data)
            is Success -> Loading(data)
            is Failure -> Loading(data)
        }

    public fun toSuccess(exception: ViewModelStateException): ViewModelState<T> =
        when (this) {
            is Idle -> Idle(exception)
            is Loading -> Success(data)
            is Success -> Success(data)
            is Failure -> Success(data)
        }

    public fun toFailure(exception: ViewModelStateException): ViewModelState<T> =
        when (this) {
            is Idle -> Idle(exception)
            is Loading -> Failure(data, exception)
            is Success -> Failure(data, exception)
            is Failure -> Failure(data, exception)
        }

    public fun onLoading(
        elseBlock: (ViewModelState<T>) -> Unit = {},
        block: (T) -> Unit = {},
    ): ViewModelState<T> = also {
        if (it is Loading) {
            block(it.data)
        }
        else {
            elseBlock(it)
        }
    }

    public fun onSuccess(
        elseBlock: (ViewModelState<T>) -> Unit = {},
        block: (T) -> Unit = {},
    ): ViewModelState<T> = also {
        if (it is Success) {
            block(it.data)
        }
        else {
            elseBlock(it)
        }
    }

    public fun onFailure(
        elseBlock: (ViewModelState<T>) -> Unit = { },
        block: (data: T, throwable: Throwable) -> Unit = { _, _ -> },
    ): ViewModelState<T> = also {
        if (it is Failure) {
            block(it.data, it.exception)
        }
        else {
            elseBlock(it)
        }
    }

    @Composable
    public fun onLoadingComposable(
        elseBlock: @Composable (ViewModelState<T>) -> Unit = {},
        block: @Composable (T) -> Unit = { LoadingCircle() },
    ): ViewModelState<T> = also {
        if (it is Loading) {
            block(it.data)
        }
        else {
            elseBlock(it)
        }
    }

    @Composable
    public fun onSuccessComposable(
        elseBlock: @Composable (ViewModelState<T>) -> Unit = {},
        block: @Composable (T) -> Unit = {},
    ): ViewModelState<T> = also {
        if (it is Success) {
            block(it.data)
        }
        else {
            elseBlock(it)
        }
    }

    @Composable
    public fun onFailureComposable(
        elseBlock: @Composable (ViewModelState<T>) -> Unit = { },
        block: @Composable (data: T, throwable: Throwable) -> Unit = { _, _ -> },
    ): ViewModelState<T> = also {
        if (it is Failure) {
            block(it.data, it.exception)
        }
        else {
            elseBlock(it)
        }
    }
}

public fun <T : Any> idle(exception: ViewModelStateException? = null): ViewModelState<T> = ViewModelState.Idle(exception)

public fun <T : Any> loading(data: T): ViewModelState.Loading<T> = ViewModelState.Loading(data)

public fun <T : Any> success(data: T): ViewModelState.Success<T> = ViewModelState.Success(data)

public fun <T : Any> failure(data: T, exception: ViewModelStateException): ViewModelState.Failure<T> = ViewModelState.Failure(data, exception)
