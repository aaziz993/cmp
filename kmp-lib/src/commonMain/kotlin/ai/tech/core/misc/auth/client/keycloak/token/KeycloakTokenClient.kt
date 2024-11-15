package ai.tech.core.misc.auth.client.keycloak.token

import ai.tech.core.misc.auth.client.keycloak.createKeycloakTokenApi
import ai.tech.core.misc.auth.client.keycloak.token.model.TokenResponse
import ai.tech.core.misc.auth.client.model.config.oauth.ClientOAuthConfig
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

public class KeycloakTokenClient(
    httpClient: HttpClient,
    public val config: ClientOAuthConfig,
) {

    private val ktorfit = Ktorfit.Builder().httpClient(
        httpClient.config {

            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }
        },
    ).baseUrl(config.address).build()

    private val api = ktorfit.createKeycloakTokenApi()

    public suspend fun getToken(username: String, password: String): TokenResponse =
        KeycloakTokenApi.getToken(
            config.realm,
            mapOf(
                "username" to username,
                "password" to password,
                "client_id" to config.clientId,
                "grant_type" to "password",
            ),
        )

    public suspend fun getTokenByRefreshToken(refreshToken: String): TokenResponse =
        KeycloakTokenApi.getToken(
            config.realm,
            mapOf(
                "refresh_token" to refreshToken,
                "client_id" to config.clientId,
                "grant_type" to "refresh_token",
            ),
        )

    public suspend fun getTokenByClientSecret(clientSecret: String): TokenResponse =
        KeycloakTokenApi.getToken(
            config.realm,
            mapOf(
                "client_secret" to clientSecret,
                "client_id" to config.clientId,
                "grant_type" to "client_credentials",
            ),
        )
}
