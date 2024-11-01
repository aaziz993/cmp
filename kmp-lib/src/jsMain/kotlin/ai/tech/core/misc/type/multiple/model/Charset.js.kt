package ai.tech.core.misc.type.multiple.model

internal fun Charset.toPlatformCharset(): String =
    when (this) {
        Charset.UTF_8 -> "UTF8"
        Charset.UTF_16 -> "UTF16"
        Charset.UTF_32 -> "UTF32"
        Charset.UTF_16BE -> "UTF16BE"
        Charset.UTF_16LE -> "UTF16LE"
        Charset.US_ASCII -> "ASCII"
        Charset.BINARY -> "BINARY"
        Charset.EUCJP -> "EUCJP"
        Charset.JIS -> "JIS"
        Charset.SJIS -> "SJIS"
        Charset.UNICODE -> "UNICODE"
        else -> throw IllegalArgumentException("Not supported charset encoding \"$name\"")
    }
