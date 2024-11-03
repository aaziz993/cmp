package ai.tech.core.misc.plugin.swagger.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class SwaggerLicenseConfig(
    val name: String? = null,
    val url: String? = null,
)
