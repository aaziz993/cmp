package ai.tech.core.misc.type.multiple.model

public interface SliceAsyncIterator<T, R> : Iterator<T> {
    public fun next(count: Int): R
}
