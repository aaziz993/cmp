package ai.tech.core.misc.plugin.hsts.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class HSTSConfig(
    val global: HSTSHostConfig? = null,
    val hostSpecific: Map<String, HSTSHostConfig>? = null,
    override val enable: Boolean? = null,
) : EnabledConfig
