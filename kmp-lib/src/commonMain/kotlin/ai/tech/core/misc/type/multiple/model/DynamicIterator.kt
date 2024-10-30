package ai.tech.core.misc.type.multiple.model

public interface DynamicIterator<T> {
    public fun hasNext(): Boolean

    public fun next(size: Int): T
}