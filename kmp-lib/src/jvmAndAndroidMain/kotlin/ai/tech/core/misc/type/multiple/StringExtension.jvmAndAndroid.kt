package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.Charset
import ai.tech.core.data.model.charsetMap

// /////////////////////////////////////////////////////ARRAY////////////////////////////////////////////////////////////
public actual fun String.encode(charset: Charset): ByteArray =
    toByteArray(
        charsetMap[charset] ?: throw IllegalArgumentException("Not supported charset encoding \"${charset.name}\""),
    )
