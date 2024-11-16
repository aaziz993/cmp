package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Config(
    var preferredHashAlgorithm: Int?,
    var preferredSymmetricAlgorithm: Int?,
    var preferredCompressionAlgorithm: Int?
)
