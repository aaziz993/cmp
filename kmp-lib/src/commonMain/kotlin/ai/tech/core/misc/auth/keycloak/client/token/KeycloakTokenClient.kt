package ai.tech.core.misc.auth.keycloak.client.token

import ai.tech.core.misc.auth.keycloak.client.token.model.TokenResponse
import ai.tech.core.misc.network.http.client.AbstractApiHttpClient
import io.ktor.client.*

public class KeycloakTokenClient(
    httpClient: HttpClient,
    public val address: String,
    public val realm: String,
    public val clientId: String
) : AbstractApiHttpClient(httpClient, address) {

    private val api = ktorfit.createKeycloakTokenApi()

    public suspend fun getToken(username: String, password: String): TokenResponse =
        api.getToken(
            realm,
            mapOf(
                "username" to username,
                "password" to password,
                "client_id" to clientId,
                "grant_type" to "password",
            ),
        )

    public suspend fun getTokenByRefreshToken(refreshToken: String): TokenResponse =
        api.getToken(
            realm,
            mapOf(
                "refresh_token" to refreshToken,
                "client_id" to clientId,
                "grant_type" to "refresh_token",
            ),
        )

    public suspend fun getTokenByClientSecret(clientSecret: String): TokenResponse =
        api.getToken(
            realm,
            mapOf(
                "client_secret" to clientSecret,
                "client_id" to clientId,
                "grant_type" to "client_credentials",
            ),
        )
}
