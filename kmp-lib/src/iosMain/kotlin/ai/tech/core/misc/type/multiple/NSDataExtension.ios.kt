package ai.tech.core.misc.type.multiple

import kotlinx.cinterop.*
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.Foundation.NSData
import platform.darwin.NSUInteger
import platform.posix.memcpy

// ////////////////////////////////////////////////////////ARRAY////////////////////////////////////////////////////////
public fun NSData.encode(): ByteArray =
    ByteArray(length.toInt()).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes!!, length)
        }
    }

// ////////////////////////////////////////////////////////STRING///////////////////////////////////////////////////////
@OptIn(BetaInteropApi::class)
public fun NSData.decode(charset: NSUInteger = NSUTF8StringEncoding): String? =
    NSString.create(data = this, encoding = charset)?.toString()
