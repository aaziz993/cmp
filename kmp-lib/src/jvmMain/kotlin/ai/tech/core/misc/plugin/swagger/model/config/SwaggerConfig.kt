package ai.tech.core.misc.plugin.swagger.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SwaggerConfig(
    val forwardRoot: Boolean? = null,
    val swaggerUrl: String? = null,
    val rootHostPath: String? = null,
    val authentication: String? = null,
    val info: SwaggerInfoConfig? = null,
    val securityScheme: Map<String, SwaggerSecuritySchemeConfig>? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
