package ai.tech.core.misc.auth.client

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import io.ktor.client.HttpClient

public interface AuthService {

    public val name: String?

    public val authHttpClient: HttpClient

    public suspend fun signIn(username: String, password: String)

    public suspend fun signOut()

    public suspend fun getUser(): User?

    public suspend fun resetPassword(password: String, newPassword: String)

    public suspend fun forgotPassword()

    public fun getUsers(): AsyncIterator<User>

    public suspend fun createUsers(users: Set<User>, password: String)

    public suspend fun updateUsers(users: Set<User>, password: String)

    public suspend fun deleteUsers(usernames: Set<String>, password: String)
}
