package ai.tech.core.misc.plugin.swagger.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class SwaggerInfoConfig(
    val title: String? = null,
    val version: String? = null,
    val description: String? = null,
    val termsOfService: String? = null,
    val contact: SwaggerContactConfig? = null,
    val license: SwaggerLicenseConfig? = null,
)
