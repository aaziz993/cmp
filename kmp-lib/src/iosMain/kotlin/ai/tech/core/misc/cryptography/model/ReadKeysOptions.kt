package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ReadKeysOptions(
    var armoredKeys: String? = null,
    var binaryKeys: ByteArray? = null
)
