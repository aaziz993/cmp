@file:OptIn(ExperimentalSerializationApi::class)

package ai.tech.core.misc.type.serialization.properties

import ai.tech.core.misc.type.multiple.toDeepMap
import kotlinx.io.IOException
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

public val String.propsComment: String
    @Throws(IOException::class)
    get() = buildString {
        val hex = HexFormat { upperCase = true }
        append("#")
        val len = length
        var current = 0
        var last = 0
        while (current < len) {
            val c = this[current]
            if (c > '\u00ff' || c == '\n' || c == '\r') {
                if (last != current) append(this.substring(last, current))
                if (c > '\u00ff') {
                    append("\\u")
                    append(c.code.toHexString(hex))
                }
                else {
                    append("\n")
                    if (c == '\r' && current != len - 1 && this[current + 1] == '\n') {
                        current++
                    }
                    if (current == len - 1 ||
                        (this[current + 1] != '#' &&
                            this[current + 1] != '!')
                    ) append("#")
                }
                last = current + 1
            }
            current++
        }
        if (last != current) append(substring(last, current))
        append("\n")
    }

@Throws(IOException::class)
public fun Map.Entry<String, String>.propsKVString(escUnicode: Boolean): String =
    "${convert(key, true, escUnicode)}=${
        /* No need to escape embedded and trailing spaces for value, hence
         * pass false to flag.
         */
        convert(value, false, escUnicode)
    }\n"

/*
 * Converts unicodes to encoded &#92;uxxxx and escapes
 * special characters with a preceding slash
 */
private fun convert(
    theString: String,
    escapeSpace: Boolean,
    escapeUnicode: Boolean
): String = buildString {
    val len = theString.length
    var bufLen = len * 2
    if (bufLen < 0) {
        bufLen = Int.Companion.MAX_VALUE
    }
    val hex = HexFormat { upperCase = true }
    for (x in 0..<len) {
        val aChar = theString[x]
        // Handle common case first, selecting the largest block that
        // avoids the specials below
        if ((aChar.code > 61) && (aChar.code < 127)) {
            if (aChar == '\\') {
                append('\\')
                append('\\')
                continue
            }
            append(aChar)
            continue
        }
        when (aChar) {
            ' ' -> {
                if (x == 0 || escapeSpace) append('\\')
                append(' ')
            }

            '\t' -> {
                append('\\')
                append('t')
            }

            '\n' -> {
                append('\\')
                append('n')
            }

            '\r' -> {
                append('\\')
                append('r')
            }

            '\u000c' -> {
                append('\\')
                append('f')
            }

            '=', ':', '#', '!' -> {
                append('\\')
                append(aChar)
            }

            else -> if (((aChar.code < 0x0020) || (aChar.code > 0x007e)) and escapeUnicode) {
                append("\\u")
                append(aChar.code.toHexString(hex))
            }
            else {
                append(aChar)
            }
        }
    }
}



