package ai.tech.core.misc.type.multiple

import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array

// //////////////////////////////////////////////////////ARRAY///////////////////////////////////////////////////////////
public fun Int8Array.toByteArray(): ByteArray = unsafeCast<ByteArray>()

public fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).toByteArray()
