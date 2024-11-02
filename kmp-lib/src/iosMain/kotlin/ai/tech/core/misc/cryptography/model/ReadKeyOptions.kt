package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ReadKeyOptions(
    var armoredKey: String? = null,
    var binaryKey: ByteArray? = null
)
