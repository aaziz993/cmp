package ai.tech.core.misc.type.multiple.model

public interface AsyncIterator<out T> {
    public suspend operator fun hasNext(): Boolean

    public suspend operator fun next(): T
}
