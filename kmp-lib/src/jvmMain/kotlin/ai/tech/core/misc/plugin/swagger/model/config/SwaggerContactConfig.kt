package ai.tech.core.misc.plugin.swagger.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SwaggerContactConfig(
    val name: String? = null,
    val url: String? = null,
    val email: String? = null,
    override val enable: Boolean = true,
) : EnabledConfig
