package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.Charset
import ai.tech.core.data.model.charsetMap

// ////////////////////////////////////////////////////////STRING////////////////////////////////////////////////////////
public actual fun ByteArray.decode(charset: Charset): String =
    toString(
        charsetMap[charset] ?: throw IllegalArgumentException("Not supported charset encoding \"${charset.name}\""),
    )
