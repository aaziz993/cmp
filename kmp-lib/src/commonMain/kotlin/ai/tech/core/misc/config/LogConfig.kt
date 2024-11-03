package ai.tech.core.misc.config

import kotlinx.serialization.Serializable

@Serializable
public data class LogConfig(
    public val level: String? = null,
)
