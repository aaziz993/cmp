package ai.tech.core.misc.type.multiple.model

import platform.Foundation.*

internal val charsetMap =
    mapOf(
        Charset.UTF_8 to NSUTF8StringEncoding,
        Charset.UTF_16 to NSUTF16StringEncoding,
        Charset.UTF_32 to NSUTF32StringEncoding,
        Charset.UTF_16BE to NSUTF16BigEndianStringEncoding,
        Charset.UTF_16LE to NSUTF16LittleEndianStringEncoding,
        Charset.UTF_32BE to NSUTF32BigEndianStringEncoding,
        Charset.UTF_32LE to NSUTF32LittleEndianStringEncoding,
        Charset.US_ASCII to NSASCIIStringEncoding,
        Charset.ISO_8859_1 to NSISOLatin1StringEncoding,
        Charset.EUCJP to NSJapaneseEUCStringEncoding,
        Charset.SJIS to NSShiftJISStringEncoding,
        Charset.UNICODE to NSUnicodeStringEncoding,
    )
