package ai.tech.core.misc.plugin.auth.oauth.model.config

import ai.tech.core.misc.auth.client.oauth.config.OAuthConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import ai.tech.core.misc.type.serialization.serializer.http.HttpMethodSerial
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
//For Keycloak you can find information about authorizeUrl, accessTokenUrl, clientId and clientSecret in Keycloak Clients page and https:/<your-keycloak-url>/realms/<your-realm-name>/.well-known/openid-configuration
public data class ServerOAuthConfig(
    override val provider: String,
    override val address: String,
    override val realm: String,
    override val clientId: String,
    val clientSecret: String,
    val authorizeUrl: String = "$address/auth/realms/$realm/protocol/openid-connect/auth",
    val accessTokenUrl: String = "$address/auth/realms/$realm/protocol/openid-connect/token",
    val requestMethod: HttpMethodSerial = HttpMethod.Post,
    val defaultScopes: List<String> = emptyList(),
    val accessTokenRequiresBasicAuth: Boolean = false,
    val passParamsInURL: Boolean = false,
    val extraAuthParameters: List<Pair<String, String>> = emptyList(),
    val extraTokenParameters: List<Pair<String, String>> = emptyList(),
    override val cookie: CookieConfig? = null,
    override val enabled: Boolean = true
) : OAuthConfig, AuthProviderConfig
