package ai.tech.core.misc.auth.client.keycloak.token

import ai.tech.core.misc.auth.client.keycloak.token.model.TokenResponse
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

internal interface KeycloakTokenApi {

    @POST("/realms/{realm}/protocol/openid-connect/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Path("realm") realm: String,
        @FieldMap fields: Map<String, String>,
    ): TokenResponse
}
