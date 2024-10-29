package ai.tech.core.presentation.viewmodel

import ai.tech.core.presentation.component.loading.LoadingCircle
import androidx.compose.runtime.Composable

public sealed class ViewState<out D : Any>(
    public open val data: D? = null,
    public open val throwable: Throwable? = null
) {
    public data object Uninitialized : ViewState<Nothing>()

    public data class Loading<out D : Any>(override val data: D) : ViewState<D>(data)

    public data class Success<out D : Any>(override val data: D) : ViewState<D>(data)

    public data class Failure<out D : Any>(override val data: D, override val throwable: Throwable) :
        ViewState<D>(data, throwable)


    public inline fun onLoading(
        elseBlock: (ViewState<D>) -> Unit = {},
        block: (D) -> Unit = {},
    ): ViewState<D> = also {
        if (it is Loading) {
            block(it.data)
        } else {
            elseBlock(it)
        }
    }

    public inline fun onSuccess(
        elseBlock: (ViewState<D>) -> Unit = {},
        block: (D) -> Unit = {},
    ): ViewState<D> = also {
        if (it is Success) {
            block(it.data)
        } else {
            elseBlock(it)
        }
    }


    public inline fun onFailure(
        elseBlock: (ViewState<D>) -> Unit = { },
        block: (data: D, throwable: Throwable) -> Unit = { _, _ -> },
    ): ViewState<D> = also {
        if (it is Failure) {
            block(it.data, it.throwable)
        } else {
            elseBlock(it)
        }
    }

    @Composable
    public inline fun onLoadingComposable(
        elseBlock: @Composable (ViewState<D>) -> Unit = {},
        block: @Composable (D) -> Unit = { LoadingCircle() },
    ): ViewState<D> = also {
        if (it is Loading) {
            block(it.data)
        } else {
            elseBlock(it)
        }
    }

    @Composable
    public inline fun onSuccessComposable(
        elseBlock: @Composable (ViewState<D>) -> Unit = {},
        block: @Composable (D) -> Unit = {},
    ): ViewState<D> = also {
        if (it is Success) {
            block(it.data)
        } else {
            elseBlock(it)
        }
    }

    @Composable
    public inline fun onFailureComposable(
        elseBlock: @Composable (ViewState<D>) -> Unit = { },
        block: @Composable (data: D, throwable: Throwable) -> Unit = { _, _ -> },
    ): ViewState<D> = also {
        if (it is Failure) {
            block(it.data, it.throwable)
        } else {
            elseBlock(it)
        }
    }
}

public fun <S : Any> loading(data: S): ViewState.Loading<S> = ViewState.Loading(data)

public fun <S : Any> success(data: S): ViewState.Success<S> = ViewState.Success(data)

public fun <S : Any> failure(data: S, throwable: Throwable): ViewState.Failure<S> = ViewState.Failure(data, throwable)

//fun <T : Any> Result<T>.toViewState(): Unit = fold(
//    onSuccess = { success(it) },
//    onFailure = { failure(it) },
//)