@file:OptIn(ExperimentalSerializationApi::class)

package ai.tech.core.misc.type.serialization

import ai.tech.core.misc.type.multiple.extension
import ai.tech.core.misc.type.serialization.properties.propsMap
import kotlinx.serialization.ExperimentalSerializationApi

public fun decoderAnyFromString(format: String): (String) -> Any? = when (format) {
    "json" -> String::jsonAny

    "yaml" -> String::yamlAny

    "properties" -> String::propsMap

    else -> throw IllegalArgumentException("Invalid format \"$format\"")
}

public fun String.decodeAnyFromString(format: String): Any? = decoderAnyFromString(format)(this)

public fun String.any(format: String): Any? = decodeAnyFromString(format)

public fun String.decodeAnyFromString(): Any? = decoderAnyFromString(extension!!)(this)

public val String.any: Any?
    get() = decodeAnyFromString(this)

public fun decoderListFromString(format: String): (String) -> List<Any?> = when (format) {
    "json" -> String::jsonList

    "yaml" -> String::yamlList

    else -> throw IllegalArgumentException("Invalid format \"$format\"")
}

public fun String.decodeListFromString(format: String): List<Any?> = decoderListFromString(format)(this)

public fun String.list(format: String): List<Any?> = decodeListFromString(format)

public fun String.decodeListFromString(): List<Any?>? = extension?.let { decoderListFromString(it)(this) }

public val String.list: List<Any?>
    get() = decodeListFromString(this)

@Suppress("UNCHECKED_CAST")
public fun decoderMapFromString(format: String): (String) -> Map<String?, Any?> = when (format) {
    "json" -> {
        { value -> value.jsonMap as Map<String?, Any?> }
    }

    "yaml" -> String::yamlMap

    "properties" -> {
        { value -> value.propsMap as Map<String?, Any?> }
    }

    else -> throw IllegalArgumentException("Invalid format \"$format\"")
}

public fun String.decodeMapFromString(format: String): Map<String?, Any?> = decoderMapFromString(format)(this)

public fun String.map(format: String): Map<String?, Any?> = decodeMapFromString(format)

public fun String.decodeMapFromString(): Map<String?, Any?> = decoderMapFromString(extension!!)(this)

public val String.map: Map<String?, Any?>
    get() = decodeMapFromString(this)
