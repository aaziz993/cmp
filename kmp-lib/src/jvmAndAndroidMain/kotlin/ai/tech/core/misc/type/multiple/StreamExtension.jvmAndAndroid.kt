package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.multiple.model.ByteArrayClosableIterator
import java.io.InputStream
import java.io.OutputStream

// /////////////////////////////////////////////////////ITERATOR////////////////////////////////////////////////////////
public fun InputStream.iterator(closeBlock: () -> Unit = {}, bufferSize: Int = DEFAULT_BUFFER_SIZE): ByteArrayClosableIterator =
    ByteArrayClosableIterator(
        ::read,
        {
            close()
            closeBlock()
        },
        bufferSize,
    )

// //////////////////////////////////////////////////OUTPUTSTREAM///////////////////////////////////////////////////////
public fun InputStream.asOutputStream(
    target: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    keepFlushing: Boolean = true,
) {
    val buf = ByteArray(bufferSize)
    var length: Int
    while ((read(buf).also { length = it }) != -1) {
        target.write(buf, 0, length)
        if (keepFlushing) {
            target.flush()
        }
    }
    if (!keepFlushing) {
        target.flush()
    }
    close()
    target.close()
}
