package ai.tech.core.misc.plugin.auth.jwt.model

import ai.tech.core.misc.plugin.auth.model.config.AuthSchemesConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
public data class JWTHS256Config(
    val secret: String? = null,
    override val issuer: String,
    override val audience: String,
    override val realm: String,
    override val jwkUri: String = "$issuer/protocol/openid-connect/certs",
    override val expiration: Duration? = null,
    override val authHeader: String? = null,
    override val authSchemes: AuthSchemesConfig? = null,
    override val cookie: CookieConfig? = null,
    override val usernameClaimKeys: List<String> = listOf("preferred_username"),
    override val rolesClaimKeys: List<String> = listOf("realm_access", "roles"),
    override val exception: Boolean = false,
    override val enable: Boolean = true
) : JWTConfig
