package ai.tech.core.misc.plugin.auth.jwt

import ai.tech.core.misc.plugin.auth.jwt.model.ServerJWTRS256Config
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.UrlJwkProvider
import java.net.URI

public class ServerJWTRS256Auth(name: String, config: ServerJWTRS256Config) : AbstractServerJWTAuth<ServerJWTRS256Config>(name, config) {

    public val jwkProvider: JwkProvider by lazy {
        UrlJwkProvider(URI(config.jwkUri).toURL())
    }
}
