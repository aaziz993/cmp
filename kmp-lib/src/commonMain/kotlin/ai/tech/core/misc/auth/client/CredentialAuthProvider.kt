package ai.tech.core.misc.auth.client

public interface CredentialAuthProvider {
    public suspend fun signIn(username: String, password: String)
}
