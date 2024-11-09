package ai.tech.core.misc.plugin.auth.basic.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DigestFunctionConfig(
    val algorithm: String,
    val salt: String,
    override val enable: Boolean = true
) : EnabledConfig
