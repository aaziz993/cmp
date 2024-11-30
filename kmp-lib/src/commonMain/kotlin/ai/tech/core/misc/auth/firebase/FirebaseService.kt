package ai.tech.core.misc.auth.firebase

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.BearerAuthService
import ai.tech.core.misc.auth.firebase.client.admin.FirebaseAdminClient
import ai.tech.core.misc.auth.model.identity.User
import ai.tech.core.misc.auth.model.bearer.BearerToken
import ai.tech.core.misc.type.multiple.toList
import io.ktor.client.*

public class FirebaseService(
    name: String,
    httpClient: HttpClient,
    address: String,
    apiKey: String,
    keyValue: AbstractKeyValue,
) : BearerAuthService(
    name,
    httpClient,
    address,
    "token",
    keyValue,
) {

    private val adminClient = FirebaseAdminClient(
        httpClient,
        address,
        apiKey,
    )

    override suspend fun getToken(username: String, password: String): BearerToken =
        adminClient.signInWithPassword(username, password)

    override suspend fun getTokenByRefreshToken(refreshToken: String): BearerToken =
        adminClient.getToken(refreshToken)

    override suspend fun getUser(): User? = adminClient.lookup("")?.asUser

    override suspend fun getUsers(): Set<User> =
        adminClient.batchGet().toList().flatten().map { it.asUser }.toSet()

    override suspend fun createUsers(users: Set<User>, password: String): Unit =
        users.forEach {
            adminClient.create(""
                it.username!!, password,
            )
        }

    override suspend fun updateUsers(users: Set<User>, password: String) {
        users.forEach { adminClient.update() }
    }

    override suspend fun deleteUsers(usernames: Set<String>, password: String) {
        usernames.forEach { adminClient.delete() }
    }

    override suspend fun resetPassword(password: String, newPassword: String) {
        TODO("Not yet implemented")
    }

    override suspend fun forgotPassword() {
        adminClient.sendOdbCode()
    }
}
