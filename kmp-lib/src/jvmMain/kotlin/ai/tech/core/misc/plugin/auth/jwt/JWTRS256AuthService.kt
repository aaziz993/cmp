@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package ai.tech.core.misc.plugin.auth.jwt

import ai.tech.core.misc.plugin.auth.jwt.model.JWTRS256Config
import com.auth0.jwk.BucketImpl
import com.auth0.jwk.GuavaCachedJwkProvider
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwk.RateLimitedJwkProvider
import com.auth0.jwk.UrlJwkProvider
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

public class JWTRS256AuthService(name: String?, config: JWTRS256Config) : AbstractJWTAuthService<JWTRS256Config>(name, config) {

    public val jwkProvider: JwkProvider = with(config) {
        var urlProvider: JwkProvider = UrlJwkProvider(
            UrlJwkProvider.urlForDomain(issuer),
            connectTimeout?.inWholeMilliseconds?.toInt(),
            readTimeout?.inWholeMilliseconds?.toInt(),
        )

        rateLimit?.let {
            urlProvider = RateLimitedJwkProvider(urlProvider, BucketImpl(it.size, it.rate.inWholeMilliseconds, TimeUnit.MILLISECONDS))
        }
        cache?.let {
            urlProvider = GuavaCachedJwkProvider(
                urlProvider, it.size ?: 5,
                (it.expiresIn
                    ?: 10.minutes).inWholeMilliseconds,
                TimeUnit.MILLISECONDS,
            )
        }
        urlProvider
    }
}
