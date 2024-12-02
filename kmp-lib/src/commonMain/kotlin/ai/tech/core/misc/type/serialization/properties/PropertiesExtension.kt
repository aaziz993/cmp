@file:OptIn(ExperimentalSerializationApi::class)

package ai.tech.core.misc.type.serialization.properties

import ai.tech.core.misc.type.multiple.toDeepMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties

public const val PROPERTIES_KEY_SEPARATOR: String = "."

public fun Properties.decodeStringMapFromCharArrays(value: Iterator<CharArray>): Map<String, String> =
    PropertiesReader(charsIterator = value).read()

public fun Properties.decodeMapFromCharArrays(value: Iterator<CharArray>): Map<String, Any?> =
    decodeStringMapFromCharArrays(value).toDeepMap(PROPERTIES_KEY_SEPARATOR)

public fun Properties.decodeStringMapFromByteArrays(value: Iterator<ByteArray>): Map<String, String> =
    PropertiesReader(value).read()

public fun Properties.decodeMapFromByteArrays(value: Iterator<ByteArray>): Map<String, Any?> =
    decodeStringMapFromByteArrays(value).toDeepMap(PROPERTIES_KEY_SEPARATOR)

public fun Properties.decodeStringMapFromByteArray(value: ByteArray): Map<String, String> =
    decodeStringMapFromByteArrays(sequenceOf(value).iterator())

public fun Properties.decodeMapFromByteArray(value: ByteArray): Map<String, Any?> =
    decodeMapFromByteArray(value).toDeepMap(PROPERTIES_KEY_SEPARATOR)

public fun Properties.decodeStringFromCharArray(value: CharArray): Map<String, String> =
    decodeStringMapFromCharArrays(sequenceOf(value).iterator())

public fun Properties.decodeFromCharArray(value: CharArray): Map<String, Any?> =
    decodeStringMapFromCharArrays(sequenceOf(value).iterator())

public fun Properties.decodeStringMapFromString(value: String): Map<String, String> =
    decodeStringFromCharArray(value.toCharArray())

public val String.propsStringMap: Map<String, Any?>
    get() = Properties.decodeStringMapFromString(this)

public fun Properties.decodeMapFromString(value: String): Map<String, Any?> =
    decodeStringMapFromString(value).toDeepMap(PROPERTIES_KEY_SEPARATOR)

public val String.propsMap: Map<String, Any?>
    get() = Properties.decodeMapFromString(this)
