package ai.tech.core.misc.plugin.auth.bearer.model.config

import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthSchemesConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class BearerAuthConfig(
    val realm: String? = null,
    val authHeader: String? = null,
    val authSchemes: AuthSchemesConfig? = null,
    override val cookie: CookieConfig? = null,
    override val enable: Boolean = true
) : AuthProviderConfig
