package ai.tech.core.misc.type.multiple.model

import ai.tech.core.DEFAULT_BUFFER_SIZE

public class ByteArrayClosableAsyncIterator(
    private val nextBlock: suspend (ByteArray) -> Int,
    private val closeBlock: () -> Unit = {},
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
) : AbstractClosableAbstractAsyncIterator<ByteArray>() {

    private val byteArray = ByteArray(bufferSize)

    override suspend fun computeNext() {
        val read = nextBlock(byteArray)
        if (read == -1) {
            close()
            closeBlock()
        }
        else {
            setNext(if (read < byteArray.size) byteArray.copyOfRange(0, read) else byteArray)
        }
    }
}
