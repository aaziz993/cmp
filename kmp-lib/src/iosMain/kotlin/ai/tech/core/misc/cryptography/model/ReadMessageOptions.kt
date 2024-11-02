package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ReadMessageOptions(
    var armoredMessage: String? = null,
    var binaryMessage: ByteArray? = null,
)
