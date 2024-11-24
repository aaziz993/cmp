package ai.tech.core.misc.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class LogConfig(
    public val level: String? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
