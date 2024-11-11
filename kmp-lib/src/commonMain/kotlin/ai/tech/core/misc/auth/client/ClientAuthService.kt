package ai.tech.core.misc.auth.client

import ai.tech.core.misc.auth.model.User
import io.ktor.client.request.*

public interface ClientAuthService {

    public suspend fun signIn(username: String, password: String)

    public suspend fun signOut()

    public suspend fun getUser(): User?

    public suspend fun getUsers(): Set<User>

    public suspend fun createUsers(users: Set<User>, password: String)

    public suspend fun updateUsers(users: Set<User>, password: String)

    public suspend fun deleteUsers(usernames: Set<String>, password: String)

    public suspend fun resetPassword(password: String, newPassword: String)

    public suspend fun forgotPassword(username: String)

    public suspend fun isSignedIn(): Boolean

    public suspend fun auth(httpRequestBuilder: HttpRequestBuilder)
}
