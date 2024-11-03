package ai.tech.core.misc.config.server

import kotlinx.serialization.Serializable

@Serializable
public data class KtorServerConfig(
    val deployment: KtorServerDeploymentConfig
)
