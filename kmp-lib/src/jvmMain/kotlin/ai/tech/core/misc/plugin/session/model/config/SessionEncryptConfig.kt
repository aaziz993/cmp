package ai.tech.core.misc.plugin.session.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SessionEncryptConfig(
    val encryptionKey: String? = null,
    val signKey: String,
    val encryptAlgorithm: String = "AES",
    val signAlgorithm: String = "HmacSHA256",
    override val enable: Boolean = true,
) : EnabledConfig
