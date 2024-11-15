package ai.tech.core.misc.type.serializer

import ai.tech.core.misc.type.multiple.splitNestedKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties

@OptIn(ExperimentalSerializationApi::class)
public fun Properties.decodeMapFromString(value: String): Map<String, String> = value.lineSequence()
    .map { it.trim() }  // Trim whitespace
    .filter { it.isNotEmpty() && !it.startsWith("#") }  // Ignore empty lines and comments
    .mapNotNull { line ->
        line.split("=", limit = 2).takeIf { it.size == 2 }?.let { (k, v) -> k to v }
    }.toMap()

@OptIn(ExperimentalSerializationApi::class)
public fun Properties.decodeNestedMapFromString(value: String): Map<String, Any?> = decodeMapFromString(value).splitNestedKey(".")
