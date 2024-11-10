package ai.tech.core.presentation.viewmodel

import ai.tech.core.presentation.component.loading.LoadingCircle
import androidx.compose.runtime.Composable

public sealed class ViewModelState<out D : Any>(
    public open val data: D? = null,
    public open val throwable: Throwable? = null
) {
    public data object Idle : ViewModelState<Nothing>()

    public data class Loading<out D : Any>(override val data: D) : ViewModelState<D>(data)

    public data class Success<out D : Any>(override val data: D) : ViewModelState<D>(data)

    public data class Failure<out D : Any>(override val data: D, override val throwable: Throwable) :
        ViewModelState<D>(data, throwable)

    public inline fun onLoading(
        elseBlock: (ViewModelState<D>) -> Unit = {},
        block: (D) -> Unit = {},
    ): ViewModelState<D> = also {
        if (it is Loading) {
            block(it.data)
        } else {
            elseBlock(it)
        }
    }

    public inline fun onSuccess(
        elseBlock: (ViewModelState<D>) -> Unit = {},
        block: (D) -> Unit = {},
    ): ViewModelState<D> = also {
        if (it is Success) {
            block(it.data)
        } else {
            elseBlock(it)
        }
    }


    public inline fun onFailure(
        elseBlock: (ViewModelState<D>) -> Unit = { },
        block: (data: D, throwable: Throwable) -> Unit = { _, _ -> },
    ): ViewModelState<D> = also {
        if (it is Failure) {
            block(it.data, it.throwable)
        } else {
            elseBlock(it)
        }
    }

    @Composable
    public inline fun onLoadingComposable(
        elseBlock: @Composable (ViewModelState<D>) -> Unit = {},
        block: @Composable (D) -> Unit = { LoadingCircle() },
    ): ViewModelState<D> = also {
        if (it is Loading) {
            block(it.data)
        } else {
            elseBlock(it)
        }
    }

    @Composable
    public inline fun onSuccessComposable(
        elseBlock: @Composable (ViewModelState<D>) -> Unit = {},
        block: @Composable (D) -> Unit = {},
    ): ViewModelState<D> = also {
        if (it is Success) {
            block(it.data)
        } else {
            elseBlock(it)
        }
    }

    @Composable
    public inline fun onFailureComposable(
        elseBlock: @Composable (ViewModelState<D>) -> Unit = { },
        block: @Composable (data: D, throwable: Throwable) -> Unit = { _, _ -> },
    ): ViewModelState<D> = also {
        if (it is Failure) {
            block(it.data, it.throwable)
        } else {
            elseBlock(it)
        }
    }
}

public fun <S : Any> loading(data: S): ViewModelState.Loading<S> = ViewModelState.Loading(data)

public fun <S : Any> success(data: S): ViewModelState.Success<S> = ViewModelState.Success(data)

public fun <S : Any> failure(data: S, throwable: Throwable): ViewModelState.Failure<S> = ViewModelState.Failure(data, throwable)

//fun <T : Any> Result<T>.toViewState(): Unit = fold(
//    onSuccess = { success(it) },
//    onFailure = { failure(it) },
//)
