package ai.tech.core.misc.type.multiple

import ai.tech.core.DEFAULT_BUFFER_SIZE
import ai.tech.core.misc.type.multiple.model.ByteArrayClosableAsyncIterator
import ai.tech.core.misc.type.multiple.model.AbstractClosableAbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.ClosableAsyncIterator
import io.ktor.utils.io.*

// ///////////////////////////////////////////////////ASYNCITERATOR//////////////////////////////////////////////////////
public fun ByteReadChannel.asyncIterator(bufferSize: Int = DEFAULT_BUFFER_SIZE): ByteArrayClosableAsyncIterator =
    ByteArrayClosableAsyncIterator(::readAvailable, ::cancel, bufferSize)
