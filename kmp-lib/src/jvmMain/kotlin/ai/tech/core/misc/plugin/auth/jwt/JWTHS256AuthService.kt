package ai.tech.core.misc.plugin.auth.jwt

import ai.tech.core.misc.auth.model.exception.UnauthenticatedAccessException
import ai.tech.core.misc.plugin.auth.jwt.model.JWTHS256Config
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.auth.*

public class JWTHS256AuthService(name: String?, config: JWTHS256Config) : AbstractJWTAuthService<JWTHS256Config>(name, config) {
    public fun jwtVerifier(httpAuthHeader: HttpAuthHeader): JWTVerifier {
        try {
            val publicKey = ServerPublicKeyCache.getPublicKey(UrlJwkProvider(config.jwkUri), httpAuthHeader)

            return JWT.require(Algorithm.RSA256(publicKey, null))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()
        } catch (e: Exception) {
            throw UnauthenticatedAccessException("Invalid token")
        }
    }
}
