package ai.tech.core.misc.auth.client

import ai.tech.core.misc.auth.model.User
import io.ktor.client.request.*

public interface ClientAuthProvider {
    public val name: String?

    public suspend fun signIn(username: String, password: String)

    public suspend fun signOut()

    public suspend fun getUser(): User?

    public suspend fun getUsers(): Set<User>

    public suspend fun createUsers(users: Set<User>, password: String)

    public suspend fun updateUsers(users: Set<User>, password: String)

    public suspend fun deleteUsers(usernames: Set<String>, password: String)

    public suspend fun resetPassword(password: String, newPassword: String)

    public suspend fun forgetPassword(username: String)

    public suspend fun onSignInExpire(block: () -> Unit)

    public suspend fun configHttpRequest(builder: HttpRequestBuilder)
}
