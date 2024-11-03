package ai.tech.core.misc.plugin.templating.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class FreeMarkerConfig(
    val classPaths: List<String>? = null,
    val filePaths: List<String>? = null,
    override val  enable: Boolean? = null,
) : EnabledConfig
