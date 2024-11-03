package ai.tech.core.misc.plugin.resources.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ResourcesConfig(
    override val enable: Boolean? = null,
) : EnabledConfig
