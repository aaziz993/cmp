package ai.tech.core.data.filesystem

import ai.tech.core.misc.type.multiple.decode

public fun readResourceBytes(path: String): ByteArray? =
    Thread.currentThread().contextClassLoader.getResourceAsStream(path)?.readAllBytes()

public fun readResourceText(path: String): String? = readResourceBytes(path)?.decode()
