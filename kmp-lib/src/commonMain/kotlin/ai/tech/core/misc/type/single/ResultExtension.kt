package ai.tech.core.misc.type.single

public inline fun <T, R> Result<T>.flatMap(
    transform: (value: T) -> Result<R>,
): Result<R> =
    if (isSuccess) {
        transform(getOrThrow())
    } else {
        Result.failure(exceptionOrNull()!!)
    }

public inline fun <T> Result<T>.flatMapOnError(
    transform: () -> Result<T>,
): Result<T> =
    if (isSuccess) {
        this
    } else {
        transform()
    }

public inline fun <T, R> Result<T>.flatMap(
    successTransform: (value: T) -> Result<R>,
    failureTransform: (exception: Throwable) -> Result<R>
): Result<R> =
    if (isSuccess) {
        successTransform(getOrThrow())
    } else {
        failureTransform(exceptionOrNull()!!)
    }


public inline fun <T, R> Result<T>.map(
    successTransform: (value: T) -> R,
    failureTransform: (exception: Throwable) -> R
): R =
    if (isSuccess) {
        successTransform(getOrThrow())
    } else {
        failureTransform(exceptionOrNull()!!)
    }

public inline fun <T> Result<T>.mapOnError(
    transform: () -> T,
): T =
    if (isSuccess) {
        getOrThrow()
    } else {
        transform()
    }

