package ai.tech.core.misc.plugin.auth.jwt

import ai.tech.core.misc.plugin.auth.jwt.model.JWTRS256Config
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.UrlJwkProvider
import java.net.URI

public class JWTRS256AuthService(name: String?, config: JWTRS256Config) : AbstractJWTAuthService<JWTRS256Config>(name, config) {

    public val jwkProvider: JwkProvider by lazy {
        UrlJwkProvider(URI(config.jwkUri).toURL())
    }
}
