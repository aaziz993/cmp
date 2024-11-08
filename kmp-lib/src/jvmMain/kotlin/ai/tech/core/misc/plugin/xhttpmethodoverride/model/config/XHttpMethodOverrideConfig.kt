package ai.tech.core.misc.plugin.xhttpmethodoverride.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class XHttpMethodOverrideConfig(
    val headerName: String? = null,
    override val enable: Boolean = true,
) : EnabledConfig
