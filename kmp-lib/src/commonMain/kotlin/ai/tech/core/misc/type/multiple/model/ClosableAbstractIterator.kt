package ai.tech.core.misc.type.multiple.model

import kotlinx.atomicfu.atomic

public abstract class ClosableAbstractIterator<T> :
    AbstractIterator<T>(),
    AutoCloseable {
    override fun close(): Unit = done()
}
