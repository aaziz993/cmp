package ai.tech.core.misc.plugin.routing.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class RoutingConfig(
    val staticRootPath: String? = null,
    val staticFiles: StaticContentConfig? = null,
    val staticResources: StaticContentConfig? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
