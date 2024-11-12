package ai.tech.core.misc.plugin.auth.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DigestConfig(
    val algorithm: String,
    override val enable: Boolean = true
) : EnabledConfig
