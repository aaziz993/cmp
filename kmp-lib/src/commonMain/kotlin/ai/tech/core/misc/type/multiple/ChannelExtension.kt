package ai.tech.core.misc.type.multiple

import ai.tech.core.DEFAULT_BUFFER_SIZE
import ai.tech.core.misc.type.multiple.model.ByteReaderClosableAsyncIterator
import io.ktor.utils.io.*

// ///////////////////////////////////////////////////ASYNCITERATOR//////////////////////////////////////////////////////
public fun ByteReadChannel.asyncIterator(bufferSize: Int = DEFAULT_BUFFER_SIZE): ByteReaderClosableAsyncIterator =
    ByteReaderClosableAsyncIterator(::readAvailable, ::cancel, bufferSize)
