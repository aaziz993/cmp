package ai.tech.core.misc.auth.client.model.config.oauth

import ai.tech.core.misc.auth.model.oauth.config.OAuthConfig
import kotlinx.serialization.Serializable

@Serializable
public data class OAuthClientConfig(
    override val address: String,
    override val realm: String,
    override val clientId: String,
    override val clientSecret: String? = null,
    override val enable: Boolean? = null,
) : OAuthConfig
