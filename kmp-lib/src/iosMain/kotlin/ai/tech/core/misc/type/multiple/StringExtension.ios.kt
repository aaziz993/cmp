package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.Charset
import ai.tech.core.misc.type.multiple.model.charsetMap
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.*

// ////////////////////////////////////////////////////////NSDATA///////////////////////////////////////////////////////
@OptIn(BetaInteropApi::class)
public fun String.decodeNSData(charset: Charset = Charset.UTF_8): NSData =
    charsetMap[charset]?.let {
        NSString.create(string = this).dataUsingEncoding(it)
    } ?: throw IllegalArgumentException("Unsupported charset encoding: ${charset.name}")

// ////////////////////////////////////////////////////////STRING///////////////////////////////////////////////////////
public actual fun String.encode(charset: Charset): ByteArray = decodeNSData(charset).toByteArray()
