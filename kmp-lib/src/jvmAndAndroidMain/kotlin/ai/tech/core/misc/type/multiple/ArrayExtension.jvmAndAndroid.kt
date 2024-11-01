package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.multiple.model.CharsetType
import ai.tech.core.misc.type.multiple.model.charsetTypeMap

// ////////////////////////////////////////////////////////STRING////////////////////////////////////////////////////////
public actual fun ByteArray.decode(charset: CharsetType): String =
    toString(
        charsetTypeMap[charset] ?: throw IllegalArgumentException("Not supported charset encoding \"${charset.name}\""),
    )
