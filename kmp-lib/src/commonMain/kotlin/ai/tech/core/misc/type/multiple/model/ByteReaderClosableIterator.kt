package ai.tech.core.misc.type.multiple.model

import ai.tech.core.DEFAULT_BUFFER_SIZE

public class ByteReaderClosableIterator(
    private val read: (ByteArray) -> Int,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
) : ClosableAbstractIterator<ByteArray>() {
    private val byteArray = ByteArray(bufferSize)

    override fun computeNext() {
        val read = read(byteArray)
        if (read == -1) {
            close()
        } else {
            setNext(if (read < byteArray.size) byteArray.copyOfRange(0, read) else byteArray)
        }
    }
}
