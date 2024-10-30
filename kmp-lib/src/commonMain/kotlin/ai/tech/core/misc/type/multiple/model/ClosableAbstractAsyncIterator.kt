package ai.tech.core.misc.type.multiple.model

import kotlinx.atomicfu.atomic

public abstract class ClosableAbstractAsyncIterator<T> :
    AbstractAsyncIterator<T>(),
    AutoCloseable {
    private var closed = atomic(false)

    internal abstract val onClose: () -> Unit

    final override fun close() {
        if (!closed.getAndSet(true)) {
            done()
            onClose()
        }
    }
}
