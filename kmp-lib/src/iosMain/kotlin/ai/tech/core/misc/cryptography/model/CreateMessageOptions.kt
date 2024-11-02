package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateMessageOptions(
    var text: String? = null,
    var binary: ByteArray? = null,
)
