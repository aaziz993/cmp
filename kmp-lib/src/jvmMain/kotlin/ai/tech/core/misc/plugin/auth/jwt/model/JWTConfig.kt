package ai.tech.core.misc.plugin.auth.jwt.model

import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthSchemesConfig
import ai.tech.core.misc.plugin.auth.model.config.ChallengeAuthProviderConfig
import ai.tech.core.misc.type.multiple.iterable.model.ContainResolution
import kotlinx.datetime.LocalDateTime

public interface JWTConfig : AuthProviderConfig, ChallengeAuthProviderConfig {

    public val realm: String
    public val issuer: String
    public val authHeader: String?
    public val authSchemes: AuthSchemesConfig?

    // Verification
    public val issuers: List<String>?
    public val subjects: List<String>?
    public val audiences: List<String>?
    public val audienceResolution: ContainResolution
    public val acceptLeeway: Long?
    public val acceptExpiresAt: Long?
    public val acceptNotBefore: Long?
    public val acceptIssuedAt: Long?
    public val jwtId: String?
    public val claimsPresence: List<String>?
    public val nullClaims: List<String>?
    public val claim: Map<String, String>?
    public val arrayClaim: Map<String, List<String>>?
    public val issuedAt: LocalDateTime?
    public val expiresAfter: LocalDateTime?

    // Claims
    public val usernameClaim: List<String>
    public val rolesClaim: List<String>?
}
