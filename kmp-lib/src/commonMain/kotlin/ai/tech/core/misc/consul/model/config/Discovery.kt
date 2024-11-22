package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class Discovery(
    override val enable: Boolean = true
) : EnabledConfig
