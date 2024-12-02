package ai.tech.core.misc.network.http

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line

public suspend inline fun ByteReadChannel.asInputStream(producer: (line: String?) -> Unit) {
    while (!isClosedForRead) {
        producer(readUTF8Line())
    }
}
