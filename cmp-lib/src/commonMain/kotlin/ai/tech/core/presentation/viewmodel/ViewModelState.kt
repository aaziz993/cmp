package ai.tech.core.presentation.viewmodel

import ai.tech.core.presentation.viewmodel.model.exception.ViewModelStateException
import arrow.core.Either

public sealed interface ViewModelState<T : Any> {

    public val data: T

    public data class Loading<T : Any>(override val data: T) : ViewModelState<T>

    public data class Success<T : Any>(override val data: T) : ViewModelState<T>

    public data class Failure<T : Any>(override val data: T, val exception: ViewModelStateException) :
        ViewModelState<T>

    public suspend fun map(
        exceptionTransform: (Throwable) -> ViewModelStateException = ::ViewModelStateException,
        block: suspend () -> T): ViewModelState<T> = try {
        Loading(block())
    }
    catch (e: Throwable) {
        Failure(data, exceptionTransform(e))
    }

    public suspend fun mapResult(
        exceptionTransform: (Throwable) -> ViewModelStateException = ::ViewModelStateException,
        block: suspend () -> Result<T>): ViewModelState<T> = block().fold(
        onSuccess = { Success(it) },
        onFailure = { Failure(data, exceptionTransform(it)) },
    )

    public suspend fun mapEither(
        exceptionTransform: (Throwable) -> ViewModelStateException = ::ViewModelStateException,
        block: suspend () -> Either<T, Throwable>
    ): ViewModelState<T> = block().fold(
        ifLeft = { Success(it) },
        ifRight = { Failure(data, exceptionTransform(it)) },
    )
}

public fun <T : Any> loading(data: T): ViewModelState.Loading<T> = ViewModelState.Loading(data)

public fun <T : Any> success(data: T): ViewModelState.Success<T> = ViewModelState.Success(data)

public fun <T : Any> failure(data: T, exception: ViewModelStateException): ViewModelState.Failure<T> = ViewModelState.Failure(data, exception)
