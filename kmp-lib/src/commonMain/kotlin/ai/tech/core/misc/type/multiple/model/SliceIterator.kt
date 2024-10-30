package ai.tech.core.misc.type.multiple.model

public interface SliceIterator<T> : Iterator<T> {
    public fun next(count: Int): T
}
