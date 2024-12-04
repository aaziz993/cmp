package ai.tech.core.misc.plugin.auth.jwt.model

import ai.tech.core.misc.cache.model.CacheConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthSchemesConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import ai.tech.core.misc.resilience.model.RateLimitConfig
import ai.tech.core.misc.type.multiple.iterable.model.ContainResolution
import kotlin.time.Duration
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
public data class JWTRS256Config(
    override val issuer: String,
    override val realm: String,
    val connectTimeout: Duration? = null,
    val readTimeout: Duration? = null,
    val rateLimit: RateLimitConfig? = null,
    val cache: CacheConfig? = null,
    override val authHeader: String? = null,
    override val authSchemes: AuthSchemesConfig? = null,
    override val issuers: List<String>? = null,
    override val subjects: List<String>? = null,
    override val audiences: List<String>? = null,
    override val audienceResolution: ContainResolution = ContainResolution.ANY,
    override val acceptLeeway: Long? = null,
    override val acceptExpiresAt: Long? = null,
    override val acceptNotBefore: Long? = null,
    override val acceptIssuedAt: Long? = null,
    override val jwtId: String? = null,
    override val claimsPresence: List<String>? = null,
    override val nullClaims: List<String>? = null,
    override val claim: Map<String, String>? = null,
    override val arrayClaim: Map<String, List<String>>? = null,
    override val issuedAt: LocalDateTime? = null,
    override val expiresAfter: LocalDateTime? = null,
    override val usernameClaim: List<String>,
    override val rolesClaim: List<String>? = null,
    override val cookie: CookieConfig? = null,
    override val exception: Boolean = false,
    override val enabled: Boolean = true
) : JWTConfig
