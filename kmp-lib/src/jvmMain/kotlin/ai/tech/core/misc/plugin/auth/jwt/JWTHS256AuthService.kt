package ai.tech.core.misc.plugin.auth.jwt

import ai.tech.core.misc.plugin.auth.jwt.model.JWTHS256Config
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

public class JWTHS256AuthService(name: String?, config: JWTHS256Config) : AbstractJWTAuthService<JWTHS256Config>(name, config) {

    public val jwtVerifier: JWTVerifier = JWT.require(Algorithm.HMAC256(config.secret))
        .ignoreIssuedAt()
        .apply {
            with(config) {
                acceptLeeway?.let(::acceptLeeway)
                acceptExpiresAt?.let(::acceptExpiresAt)
                acceptNotBefore?.let(::acceptNotBefore)
                acceptIssuedAt?.let(::acceptIssuedAt)
            }
        }
        .build()
}
