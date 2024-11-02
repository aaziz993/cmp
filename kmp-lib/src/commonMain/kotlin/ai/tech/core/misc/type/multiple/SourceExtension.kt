package ai.tech.core.misc.type.multiple

import ai.tech.core.DEFAULT_BUFFER_SIZE
import ai.tech.core.misc.type.multiple.model.ByteReaderClosableIterator
import okio.Buffer
import okio.Source

// /////////////////////////////////////////////////////ITERATOR////////////////////////////////////////////////////////
public fun Source.iterator(bufferSize: Int = DEFAULT_BUFFER_SIZE): ByteReaderClosableIterator =
    Buffer().let { buffer ->
        ByteReaderClosableIterator(
            { array ->
                read(buffer, array.size.toLong())
                    .also {
                        buffer.readByteArray(it).copyInto(array)
                    }.toInt()
            },
            ::close, bufferSize,
        )
    }
