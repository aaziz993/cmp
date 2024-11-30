package ai.tech.core.misc.auth.client

import ai.tech.core.misc.auth.model.identity.User
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import io.ktor.client.HttpClient

public interface AuthService {

    public val name: String?

    public val authHttpClient: HttpClient

    public suspend fun signOut()

    public suspend fun getUser(): User?
}
