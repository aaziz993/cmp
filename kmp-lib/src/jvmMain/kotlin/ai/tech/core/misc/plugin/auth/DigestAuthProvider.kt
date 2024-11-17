package ai.tech.core.misc.plugin.auth

public interface DigestAuthProvider {

    public suspend fun digestProvider(userName: String, realm: String): ByteArray?
}
