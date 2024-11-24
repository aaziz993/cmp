package ai.tech.core.misc.plugin.conditionalheaders.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ConditionalHeadersConfig(
    val versionHeadersPath: String? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
