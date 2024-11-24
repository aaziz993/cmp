package ai.tech.core.misc.plugin.authheadresponse.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class AutoHeadResponseConfig(
    override val enabled: Boolean = true,
) : EnabledConfig
