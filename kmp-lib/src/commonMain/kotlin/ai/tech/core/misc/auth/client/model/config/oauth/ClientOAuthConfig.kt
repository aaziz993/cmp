package ai.tech.core.misc.auth.client.model.config.oauth

import ai.tech.core.misc.auth.model.oauth.config.OAuthConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ClientOAuthConfig(
    override val provider: String,
    override val address: String,
    override val realm: String,
    override val clientId: String,
    override val clientSecret: String? = null,
) : OAuthConfig