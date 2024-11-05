package ai.tech.core.misc.auth.server.jwt.model

import ai.tech.core.misc.auth.server.model.config.AuthSchemesConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
public data class ServerJWTRS256Config(
    val privateKey: String? = null,
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
    override val throwException: Boolean? = null,
) : ServerJWTConfig
