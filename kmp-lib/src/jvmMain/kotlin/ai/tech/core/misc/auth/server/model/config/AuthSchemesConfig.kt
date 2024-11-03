package ai.tech.core.misc.auth.server.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class AuthSchemesConfig(
     val defaultScheme: String = "Bearer",
     val additionalSchemes: List<String> = emptyList(),
)
