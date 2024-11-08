package ai.tech.core.misc.plugin.defaultheaders.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DefaultHeadersConfig(
    val headers: Map<String, String>? = null,
    override val enable: Boolean = true,
) : EnabledConfig
