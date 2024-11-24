package ai.tech.core.misc.plugin.statuspages.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class StatusPagesConfig(
    val status: List<StatusConfig>? = null,
    val statusFile: List<StatusFileConfig>? = null,
    override val enabled: Boolean = true
) : EnabledConfig
