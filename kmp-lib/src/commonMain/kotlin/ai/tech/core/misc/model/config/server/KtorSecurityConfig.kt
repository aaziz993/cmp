package ai.tech.core.misc.model.config.server

import kotlinx.serialization.Serializable

@Serializable
public data class KtorSecurityConfig(
    val ssl: KtorSecuritySSLConfig? = null,
)
