package ai.tech.core.misc.auth.server.jwt.model

import ai.tech.core.misc.auth.server.ServerAuthProviderConfig
import ai.tech.core.misc.auth.server.model.config.AuthSchemesConfig
import kotlin.time.Duration

public interface ServerJWTConfig : ServerAuthProviderConfig {
    public val issuer: String
    public val audience: String
    public val realm: String
    public val jwkUri: String
    public val expiration: Duration?
    public val authHeader: String?
    public val authSchemes: AuthSchemesConfig?
    public val usernameClaimKeys: List<String>
    public val rolesClaimKeys: List<String>
    public val throwException: Boolean?
}
