package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.letIf
import ai.tech.core.misc.type.single.toIntLSB
import java.io.InputStream
import java.io.OutputStream

// /////////////////////////////////////////////////////INPUTSTREAM//////////////////////////////////////////////////////
public fun Iterator<Byte>.asInputStream(): InputStream = IteratorInputStream(this)

private class IteratorInputStream(
    private val iterator: Iterator<Byte>,
) : InputStream() {
    override fun read(): Int =
        if (iterator.hasNext()) {
            iterator.next().toIntLSB()
        } else {
            -1
        }

    override fun read(
        b: ByteArray,
        off: Int,
        len: Int,
    ): Int = iterator.next(len) { i, e -> b[off + i] = e }.letIf({ it == 0 }) { -1 }

    override fun available(): Int = if (iterator.hasNext()) 1 else 0
}

// /////////////////////////////////////////////////////OUTPUTSTREAM//////////////////////////////////////////////////////
public fun Iterator<ByteArray>.asOutputStream(
    outputStream: OutputStream,
    keepFlushing: Boolean = true,
) {
    forEach {
        outputStream.write(it)
        if (keepFlushing) {
            outputStream.flush()
        }
    }
    if (!keepFlushing) {
        outputStream.flush()
    }
    outputStream.close()
}
