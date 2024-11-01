package ai.tech.core.misc.type.multiple.model

public abstract class ClosableAbstractAsyncIterator<T> :
    AbstractAsyncIterator<T>(),
    AutoCloseable {
    override fun close(): Unit = done()
}
