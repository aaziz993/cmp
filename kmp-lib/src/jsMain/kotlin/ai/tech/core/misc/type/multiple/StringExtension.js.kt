package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.multiple.model.Charset
import ai.tech.core.misc.type.multiple.model.toPlatformCharset
import ai.tech.core.misc.type.single.Object
import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array

// //////////////////////////////////////////////////////ARRAY///////////////////////////////////////////////////////////
public fun Int8Array.toByteArray(): ByteArray = unsafeCast<ByteArray>()

public fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).toByteArray()

public actual fun String.encode(charset: Charset): ByteArray =
    (
        convert(
            this,
            Object {
                to = charset.toPlatformCharset()
                type = "array"
            },
        ) as ArrayBuffer
    ).toByteArray()
