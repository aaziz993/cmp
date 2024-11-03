package ai.tech.core.misc.plugin.session.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class SessionEncryptConfig(
    val encryptionKey: String? = null,
    val signKey: String,
    val encryptAlgorithm: String = "AES",
    val signAlgorithm: String = "HmacSHA256",
)
