package ai.tech.core.misc.type.multiple.model

import ai.tech.core.DEFAULT_BUFFER_SIZE

public class ByteReaderClosableIterator(
    private val readBlock: (ByteArray) -> Int,
    private val closeBlock: () -> Unit = {},
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
) : ClosableAbstractIterator<ByteArray>() {
    private val byteArray = ByteArray(bufferSize)

    override fun computeNext() {
        val read = readBlock(byteArray)
        if (read == -1) {
            close()
            closeBlock()
        } else {
            setNext(if (read < byteArray.size) byteArray.copyOfRange(0, read) else byteArray)
        }
    }
}
