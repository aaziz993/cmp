package ai.tech.core.misc.type.multiple.model

private object State {
    const val NOT_READY: Int = 0
    const val READY: Int = 1
    const val DONE: Int = 2
    const val FAILED: Int = 3
}

public abstract class AbstractAsyncIterator<T> : AsyncIterator<T> {
    private var state = State.NOT_READY
    private var nextValue: T? = null

    override suspend fun hasNext(): Boolean {
        return when (state) {
            State.DONE -> false
            State.READY -> true
            State.NOT_READY -> tryToComputeNext()
            else -> throw IllegalArgumentException("hasNext called when the iterator is in the FAILED state.")
        }
    }

    override suspend fun next(): T {
        if (state == State.READY) {
            state = State.NOT_READY
            @Suppress("UNCHECKED_CAST")
            return nextValue as T
        }
        if (state == State.DONE || !tryToComputeNext()) {
            throw NoSuchElementException()
        }
        state = State.NOT_READY
        @Suppress("UNCHECKED_CAST")
        return nextValue as T
    }

    private suspend fun  tryToComputeNext(): Boolean {
        state = State.FAILED
        computeNext()
        return state == State.READY
    }

    protected abstract suspend fun computeNext(): Unit

    protected fun setNext(value: T): Unit {
        nextValue = value
        state = State.READY
    }

    protected fun done() {
        state = State.DONE
    }
}
