package ai.tech.core.misc.type.multiple

import kotlin.text.toInt
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.darwin.NSUInteger
import kotlinx.cinterop.*
import platform.posix.memcpy

// ////////////////////////////////////////////////////////ARRAY////////////////////////////////////////////////////////
public fun NSData.encode(): ByteArray =
    ByteArray(length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), bytes, length)
        }
    }

// ////////////////////////////////////////////////////////STRING///////////////////////////////////////////////////////
public fun NSData.decode(charset: NSUInteger = NSUTF8StringEncoding): String? =
    NSString.create(data = this, encoding = charset)?.toString()
