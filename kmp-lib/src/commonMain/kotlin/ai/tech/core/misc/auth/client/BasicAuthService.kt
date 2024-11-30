package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.data.keyvalue.get
import ai.tech.core.misc.auth.client.model.Credentials
import ai.tech.core.misc.auth.model.identity.User
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*

public class BasicAuthService(
    httpClient: HttpClient,
    public val realm: String,
    private val keyValue: AbstractKeyValue,
) : AuthService {

    private val credentialsKey = "${name?.let { "${it}_" }.orEmpty()}credentials"

    override val authHttpClient: HttpClient = httpClient.config {
        install(Auth) {
            basic {
                this.realm = realm
                credentials { getCredentials()?.let { BasicAuthCredentials(it.username, it.password) } }
            }
        }
    }

    private suspend fun getCredentials(): Credentials? = keyValue.get(credentialsKey)

    override suspend fun signIn(username: String, password: String): Unit = keyValue.set(credentialsKey, Credentials(username, password))

    override suspend fun signOut(): Unit = keyValue.remove(credentialsKey)

    override suspend fun getUser(): User? =
        getCredentials()?.let { User(it.username) }

    override suspend fun resetPassword(password: String, newPassword: String) {
        TODO("Not yet implemented")
    }

    override suspend fun forgotPassword(): Nothing = throw UnsupportedOperationException()

    override fun getUsers(): AsyncIterator<User> {
    }

    override suspend fun createUsers(users: Set<User>, password: String) {
    }

    override suspend fun updateUsers(users: Set<User>, password: String) {
    }

    override suspend fun deleteUsers(usernames: Set<String>, password: String) {
    }
}
