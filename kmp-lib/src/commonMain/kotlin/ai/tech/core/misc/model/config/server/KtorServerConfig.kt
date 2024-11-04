package ai.tech.core.misc.model.config.server

import kotlinx.serialization.Serializable

@Serializable
public data class KtorServerConfig(
    val deployment: KtorServerDeploymentConfig,
    val environment: String,
    val security: KtorSecurityConfig? = null
)
