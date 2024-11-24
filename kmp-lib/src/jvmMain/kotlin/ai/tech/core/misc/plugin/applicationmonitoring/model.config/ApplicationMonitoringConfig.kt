package ai.tech.core.misc.plugin.applicationmonitoring.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ApplicationMonitoringConfig(
    override val enabled: Boolean = true,
) : EnabledConfig
