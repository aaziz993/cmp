package ai.tech.core.misc.plugin.swagger.model.config

import io.github.smiley4.ktorswaggerui.data.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import kotlinx.serialization.Serializable

@Serializable
public data class SwaggerSecuritySchemeConfig(
    val type: AuthType? = null,
    val name: String? = null,
    val location: AuthKeyLocation? = null,
    val scheme: AuthScheme? = null,
    val bearerFormat: String? = null,
    val openIdConnectUrl: String? = null,
    val description: String? = null,
)
