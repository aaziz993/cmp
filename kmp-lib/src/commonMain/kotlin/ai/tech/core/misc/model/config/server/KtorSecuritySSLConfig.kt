package ai.tech.core.misc.model.config.server

import kotlinx.serialization.Serializable

@Serializable
public data class KtorSecuritySSLConfig(
    override val keyStore: String,
    override val keyStorePassword: String,
    override val keyAlias: String,
    override val privateKeyPassword: String,
) : ServerSSLConfig
