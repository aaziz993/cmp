package ai.tech.core.misc.plugin.auth.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class AuthSchemesConfig(
     val defaultScheme: String = "Bearer",
     val additionalSchemes: List<String> = emptyList(),
)
