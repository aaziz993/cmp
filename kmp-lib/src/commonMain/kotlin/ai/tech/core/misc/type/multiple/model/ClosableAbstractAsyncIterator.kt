package ai.tech.core.misc.type.multiple.model

import kotlinx.atomicfu.atomic

public abstract class ClosableAbstractAsyncIterator<T> :
    AbstractAsyncIterator<T>(),
    AutoCloseable {
    final override fun close(): Unit = done()

}
