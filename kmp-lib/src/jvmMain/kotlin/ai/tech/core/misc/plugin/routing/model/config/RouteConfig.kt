package ai.tech.core.misc.plugin.routing.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import core.io.model.http.server.routing.StaticContentConfig
import kotlinx.serialization.Serializable

@Serializable
public data class RoutingConfig(
    val staticRootPath: String? = null,
    val staticFiles: StaticContentConfig? = null,
    val staticResources: StaticContentConfig? = null,
    override val enable: Boolean = true,
) : EnabledConfig
