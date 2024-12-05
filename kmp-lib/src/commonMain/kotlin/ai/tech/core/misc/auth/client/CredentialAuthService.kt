package ai.tech.core.misc.auth.client

public interface CredentialAuthService {
    public suspend fun signIn(username: String, password: String)
}
