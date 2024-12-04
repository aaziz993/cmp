package ai.tech.core.misc.type.multiple.model

public abstract class AbstractClosableAbstractIterator<T> :
    AbstractIterator<T>(),
    AutoCloseable {
    override fun close(): Unit = done()
}
