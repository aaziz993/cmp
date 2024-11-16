package ai.tech.core.misc.auth.client

import ai.tech.core.misc.auth.model.User

public interface ClientAuthService {

    public val name: String

    public suspend fun authIn(username: String, password: String)

    public suspend fun authOut()

    public suspend fun getUser(): User?

    public suspend fun getUsers(): Set<User>

    public suspend fun createUsers(users: Set<User>, password: String)

    public suspend fun updateUsers(users: Set<User>, password: String)

    public suspend fun deleteUsers(usernames: Set<String>, password: String)

    public suspend fun resetPassword(password: String, newPassword: String)

    public suspend fun forgotPassword()

    public suspend fun isAuth(): Boolean
}
