package ai.tech.core.misc.cryptography.model

internal data class ReadSignatureOptions(
    var armoredSignature: String? = null,
    var binarySignature: ByteArray? = null,
)
