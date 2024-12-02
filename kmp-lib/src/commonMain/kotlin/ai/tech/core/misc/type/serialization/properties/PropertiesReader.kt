package ai.tech.core.misc.type.serialization.properties

import kotlinx.io.IOException

internal class PropertiesReader(
    private val bytesIterator: Iterator<ByteArray>? = null,
    private val charsIterator: Iterator<CharArray>? = null
) {

    private var lineBuf: CharArray = CharArray(1024)
    private lateinit var byteBuf: ByteArray
    private lateinit var charBuf: CharArray
    private var limit = 0
    private var off = 0

    private fun nextBytes() {
        byteBuf = if (bytesIterator!!.hasNext()) {
            bytesIterator.next()
        } else {
            byteArrayOf()
        }
        limit = byteBuf.size
    }

    private fun nextChars() {
        charBuf = if (charsIterator!!.hasNext()) {
            charsIterator.next()
        } else {
            charArrayOf()
        }
        limit = charBuf.size
    }

    private fun next() {
        if (bytesIterator != null) {
            nextBytes()
        }
        if (charsIterator != null) {
            nextChars()
        }
    }

    @Throws(IOException::class)
    private fun readLine(): Int {
        // use locals to optimize for interpreted performance
        var len = 0

        var skipWhiteSpace = true
        var appendedLineBegin = false
        var precedingBackslash = false
        val fromStream = bytesIterator != null
        var c: Char

        while (true) {
            if (off >= limit) {
                next()

                if (limit <= 0) {
                    if (len == 0) {
                        return -1
                    }
                    return if (precedingBackslash) len - 1 else len
                }
                off = 0
            }

            // (char)(byte & 0xFF) is equivalent to calling a ISO8859-1 decoder.
            c = if (fromStream) (byteBuf[off++].toInt() and 0xFF).toChar() else charBuf[off++]

            if (skipWhiteSpace) {
                if (c == ' ' || c == '\t' || c == '\u000c') {
                    continue
                }
                if (!appendedLineBegin && (c == '\r' || c == '\n')) {
                    continue
                }
                skipWhiteSpace = false
                appendedLineBegin = false
            }
            if (len == 0) { // Still on a new logical line
                if (c == '#' || c == '!') {
                    // Comment, quickly consume the rest of the line

                    // When checking for new line characters a range check,
                    // starting with the higher bound ('\r') means one less
                    // branch in the common case.

                    commentLoop@ while (true) {
                        if (fromStream) {
                            var b: Byte
                            while (off < limit) {
                                b = byteBuf[off++]
                                if (b <= '\r'.code.toByte() && (b == '\r'.code.toByte() || b == '\n'.code.toByte())) break@commentLoop
                            }
                            if (off == limit) {
                                nextBytes()
                                if (limit <= 0) { // EOF
                                    return -1
                                }
                                off = 0
                            }
                        } else {
                            while (off < limit) {
                                c = charBuf[off++]
                                if (c <= '\r' && (c == '\r' || c == '\n')) break@commentLoop
                            }
                            if (off == limit) {
                                nextChars()
                                if (limit <= 0) { // EOF
                                    return -1
                                }
                                off = 0
                            }
                        }
                    }
                    skipWhiteSpace = true
                    continue
                }
            }

            if (c != '\n' && c != '\r') {
                lineBuf[len++] = c
                if (len == lineBuf.size) {
                    lineBuf = lineBuf.copyOf(len.coerceAtLeast(1))
                }
                // flip the preceding backslash flag
                precedingBackslash = if (c == '\\') !precedingBackslash else false
            } else {
                // reached EOL
                if (len == 0) {
                    skipWhiteSpace = true
                    continue
                }
                if (off >= limit) {
                    next()
                    off = 0
                    if (limit <= 0) { // EOF
                        return if (precedingBackslash) len - 1 else len
                    }
                }
                if (precedingBackslash) {
                    // backslash at EOL is not part of the line
                    len -= 1
                    // skip leading whitespace characters in the following line
                    skipWhiteSpace = true
                    appendedLineBegin = true
                    precedingBackslash = false
                    // take care not to include any subsequent \n
                    if (c == '\r') {
                        if (fromStream) {
                            if (byteBuf[off] == '\n'.code.toByte()) {
                                off++
                            }
                        } else {
                            if (charBuf[off] == '\n') {
                                off++
                            }
                        }
                    }
                } else {
                    return len
                }
            }
        }
    }

    @Throws(IOException::class)
    fun read(): Map<String, String> = mutableMapOf<String, String>().apply {
        val outBuffer = StringBuilder()
        var limit: Int
        var keyLen: Int
        var valueStart: Int
        var hasSep: Boolean
        var precedingBackslash: Boolean

        while (readLine().also { limit = it } >= 0) {
            keyLen = 0
            valueStart = limit
            hasSep = false

            //println("line=<" + line.substring(0, limit) + ">")
            precedingBackslash = false
            while (keyLen < limit) {
                val c = lineBuf[keyLen]
                //need check if escaped.
                if ((c == '=' || c == ':') && !precedingBackslash) {
                    valueStart = keyLen + 1
                    hasSep = true
                    break
                } else if ((c == ' ' || c == '\t' || c == '\u000c') && !precedingBackslash) {
                    valueStart = keyLen + 1
                    break
                }
                precedingBackslash = if (c == '\\') {
                    !precedingBackslash
                } else {
                    false
                }
                keyLen++
            }
            while (valueStart < limit) {
                val c = lineBuf[valueStart]
                if (c != ' ' && c != '\t' && c != '\u000c') {
                    if (!hasSep && (c == '=' || c == ':')) {
                        hasSep = true
                    } else {
                        break
                    }
                }
                valueStart++
            }
            val key = loadConvert(0, keyLen, outBuffer)
            val value = loadConvert(valueStart, limit - valueStart, outBuffer)
            put(key, value)
        }
    }

    private fun loadConvert(off: Int, len: Int, out: StringBuilder): String {
        var off = off
        var aChar: Char
        val end = off + len
        val start = off
        while (off < end) {
            aChar = lineBuf[off++]
            if (aChar == '\\') {
                break
            }
        }
        if (off == end) { // No backslash
            return lineBuf.concatToString(start, start + len)
        }

        // backslash found at off - 1, reset the shared buffer, rewind offset
        out.setLength(0)
        off--
        out.appendRange(lineBuf, start, off)

        while (off < end) {
            aChar = lineBuf[off++]
            if (aChar == '\\') {
                // No need to bounds check since lines are with excluded
                // unescaped \s at the end of the line
                aChar = lineBuf[off++]
                if (aChar == 'u') {
                    // Read the xxxx
                    require(off <= end - 4) { "Malformed \\uxxxx encoding." }
                    var value = 0
                    for (i in 0..3) {
                        aChar = lineBuf[off++]
                        value = when (aChar) {
                            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> (value shl 4) + aChar.code - '0'.code
                            'a', 'b', 'c', 'd', 'e', 'f' -> (value shl 4) + 10 + aChar.code - 'a'.code
                            'A', 'B', 'C', 'D', 'E', 'F' -> (value shl 4) + 10 + aChar.code - 'A'.code
                            else -> throw IllegalArgumentException("Malformed \\uxxxx encoding.")
                        }
                    }
                    out.append(value.toChar())
                } else {
                    when (aChar) {
                        't' -> aChar = '\t'
                        'r' -> aChar = '\r'
                        'n' -> aChar = '\n'
                        'f' -> aChar = '\u000c'
                    }
                    out.append(aChar)
                }
            } else {
                out.append(aChar)
            }
        }
        return out.toString()
    }

}
