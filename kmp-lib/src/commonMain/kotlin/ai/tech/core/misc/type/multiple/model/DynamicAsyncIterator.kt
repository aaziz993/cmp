package ai.tech.core.misc.type.multiple.model

public interface DynamicAsyncIterator<T> {
    public suspend fun hasNext(): Boolean

    public suspend fun next(size: Int): T
}
