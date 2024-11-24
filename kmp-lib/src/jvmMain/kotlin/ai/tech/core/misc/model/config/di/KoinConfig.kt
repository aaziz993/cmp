package ai.tech.core.misc.model.config.di

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.model.config.LogConfig
import kotlinx.serialization.Serializable

@Serializable
public data class KoinConfig(
    val logging: LogConfig? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
