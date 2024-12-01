@file:OptIn(ExperimentalSerializationApi::class)

package ai.tech.core.misc.type.serialization

import ai.tech.core.misc.type.multiple.extension
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.properties.Properties
import net.mamoe.yamlkt.Yaml

@Suppress("UNCHECKED_CAST")
public fun decoderMapFromString(format: String): (String) -> Map<String?, Any?> = when (format) {
    "json" -> {
        { Json.Default.decodeMapFromString(it) as Map<String?, Any?> }
    }

    "yaml" -> {
        { Yaml.Default.decodeMapFromString(it) }
    }

    "properties" -> {
        { Properties.Default.decodeMapFromString(it) as Map<String?, Any?> }
    }

    else -> throw IllegalArgumentException("Invalid format \"$format\"")
}

public fun String.decodeMapFromString(format: String): Map<String?, Any?> = decoderMapFromString(format)(this)

public fun String.decodeMapFromString(): Map<String?, Any?> = decoderMapFromString(extension!!)(this)

public fun decoderListFromString(format: String): (String) -> List<Any?> = when (format) {
    "json" -> {
        { Json.Default.decodeListFromString(it) }
    }

    "yaml" -> {
        { Yaml.Default.decodeListFromString(it) }
    }

    else -> throw IllegalArgumentException("Invalid format \"$format\"")
}

public fun String.decodeListFromString(format: String): List<Any?> = decoderListFromString(format)(this)

public fun String.decodeListFromString(): List<Any?>? = extension?.let { decoderListFromString(it)(this) }

public fun decoderAnyFromString(format: String): (String) -> Any? = when (format) {
    "json" -> {
        { Json.Default.decodeAnyFromString(it) }
    }

    "yaml" -> {
        { Yaml.Default.decodeAnyFromString(it) }
    }

    "properties" -> {
        { Properties.Default.decodeMapFromString(it) }
    }

    else -> throw IllegalArgumentException("Invalid format \"$format\"")
}

public fun String.decodeAnyFromString(format: String): Any? = decoderAnyFromString(format)(this)

public fun String.decodeAnyFromString(): Any? = decoderAnyFromString(extension!!)(this)
