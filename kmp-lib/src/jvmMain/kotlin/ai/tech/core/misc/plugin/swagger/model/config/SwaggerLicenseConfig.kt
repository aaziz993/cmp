package ai.tech.core.misc.plugin.swagger.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SwaggerLicenseConfig(
    val name: String? = null,
    val url: String? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
