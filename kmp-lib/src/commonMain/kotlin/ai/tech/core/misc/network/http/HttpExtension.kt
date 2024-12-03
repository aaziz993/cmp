package ai.tech.core.misc.network.http

import io.ktor.utils.io.*
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

public val ByteReadChannel.inputStream: Flow<String?>
    get() = flow {
        while (!isClosedForRead) {
            emit(readUTF8Line())
        }
    }
