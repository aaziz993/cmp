package ai.tech.core.misc.plugin.auth.jwt.model

import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthSchemesConfig
import ai.tech.core.misc.plugin.auth.model.config.ChallengeAuthProviderConfig
import kotlin.time.Duration

public interface JWTConfig : AuthProviderConfig, ChallengeAuthProviderConfig {

    public val issuer: String
    public val audience: String
    public val realm: String
    public val jwkUri: String
    public val expiration: Duration?
    public val authHeader: String?
    public val authSchemes: AuthSchemesConfig?
    public val usernameClaimKeys: List<String>
    public val rolesClaimKeys: List<String>
}
