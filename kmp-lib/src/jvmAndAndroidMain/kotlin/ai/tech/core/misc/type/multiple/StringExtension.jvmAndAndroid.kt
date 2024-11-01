package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.multiple.model.CharsetType
import ai.tech.core.misc.type.multiple.model.charsetTypeMap

// /////////////////////////////////////////////////////ARRAY////////////////////////////////////////////////////////////
public actual fun String.encode(charset: CharsetType): ByteArray =
    toByteArray(
        charsetTypeMap[charset] ?: throw IllegalArgumentException("Not supported charset encoding \"${charset.name}\""),
    )
