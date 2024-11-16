package ai.tech.core.misc.auth.keycloak.client.admin

import ai.tech.core.misc.auth.keycloak.client.admin.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.keycloak.client.admin.model.ResetPassword
import ai.tech.core.misc.auth.keycloak.client.admin.model.RoleRepresentation
import ai.tech.core.misc.auth.keycloak.client.admin.model.UserInfo
import ai.tech.core.misc.auth.keycloak.client.admin.model.UserRepresentation
import ai.tech.core.misc.type.serializer.encodeAnyToString
import ai.tech.core.misc.type.serializer.encodeToAny
import de.jensklingenberg.ktorfit.Ktorfit
import kotlin.collections.orEmpty
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class KeycloakAdminClient(ktorfit: Ktorfit, public val realm: String) {

    private val api = ktorfit.createKeycloakAdminApi()

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    public suspend fun createUser(userRepresentation: UserRepresentation): Unit =
        api.createUser(realm, userRepresentation)

    public suspend fun getUsers(userRepresentation: UserRepresentation? = null, exact: Boolean? = null): Set<UserRepresentation> =
        api.getUsers(
            realm,
            userRepresentation?.let {
                listOfNotNull(
                    exact?.let { "exact" to it.toString() },
                    (it.attributes as Map<*, *>?)?.let {
                        "q" to it.entries.joinToString(" ") { (k, v) -> "$k:${json.encodeAnyToString(v)}" }
                    },
                ) + (json.encodeToAny(it) as Map<*, *>).filter { (k, v) -> k !== "attributes" && v != null }
                    .map { (k, v) ->
                        k.toString() to json.encodeAnyToString(v)
                    }
            }?.toMap().orEmpty(),
        )

    public suspend fun updateUser(userRepresentation: UserRepresentation): Unit =
        api.updateUser(realm, userRepresentation.id!!, userRepresentation)

    public suspend fun deleteUser(userId: String): Unit = api.deleteUser(realm, userId)

    public suspend fun getUserInfo(): UserInfo = api.getUserInfo(realm)

    public suspend fun getUserRealmRoles(userId: String): Set<RoleRepresentation> =
        api.getUserRealmRoles(realm, userId)

    public suspend fun resetPassword(userId: String, resetPassword: ResetPassword): Unit =
        api.resetPassword(realm, userId, resetPassword)

    public suspend fun executeActionsEmail(userId: String, executeActionsEmail: ExecuteActionsEmail): Unit =
        api.updatePassword(realm, userId, executeActionsEmail)
}
