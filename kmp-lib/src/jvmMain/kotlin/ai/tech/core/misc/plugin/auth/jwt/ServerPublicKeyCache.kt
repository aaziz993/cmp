package ai.tech.core.misc.plugin.auth.jwt

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import io.ktor.http.auth.*
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.ConcurrentHashMap

/**
 * Keeps a cache of the public keys for validation of the JWT signature.
 * When key rotation is configured
 */
public object ServerPublicKeyCache {
    private const val BEARER_PREFIX = "Bearer "
    private val publicKeyCache = ConcurrentHashMap<String, RSAPublicKey>()

    public fun getPublicKey(jwkProvider: JwkProvider, authHeader: HttpAuthHeader): RSAPublicKey {
        val jwtToken = JWT.decode(authHeader.render().substring(BEARER_PREFIX.length))
        val keyId = jwtToken.keyId
        return publicKeyCache.computeIfAbsent(keyId) {
            println("retrieving publicKey for $keyId")
            jwkProvider.get(jwtToken.keyId).publicKey as RSAPublicKey
        }
    }
}
