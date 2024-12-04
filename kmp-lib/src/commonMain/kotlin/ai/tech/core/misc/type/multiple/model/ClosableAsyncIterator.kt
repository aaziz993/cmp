package ai.tech.core.misc.type.multiple.model

public class ClosableAsyncIterator<T>(
    private val hasBlock: suspend () -> Boolean,
    private val nextBlock: suspend () -> T,
    private val closeBlock: () -> Unit = {},
) : AbstractClosableAbstractAsyncIterator<T>() {

    override suspend fun computeNext() {
        if (hasBlock()) {
            return setNext(nextBlock())
        }

        close()
        closeBlock()
    }
}
