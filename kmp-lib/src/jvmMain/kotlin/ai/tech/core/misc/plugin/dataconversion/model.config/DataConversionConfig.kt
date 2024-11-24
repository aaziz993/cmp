package ai.tech.core.misc.plugin.dataconversion.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DataConversionConfig(
    override val enabled: Boolean = true,
) : EnabledConfig
