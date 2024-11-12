package ai.tech.core.data.filesystem

import ai.tech.core.misc.type.multiple.decode
import java.io.InputStream
import java.util.Properties

public fun getResourceAsStream(path: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(path)

public fun readResourceProperties(path: String): Properties =
    Properties().apply {
        load(getResourceAsStream(path))
    }

public fun readResourceBytes(path: String): ByteArray? =
    getResourceAsStream(path)?.readAllBytes()

public fun readResourceText(path: String): String? = readResourceBytes(path)?.decode()
