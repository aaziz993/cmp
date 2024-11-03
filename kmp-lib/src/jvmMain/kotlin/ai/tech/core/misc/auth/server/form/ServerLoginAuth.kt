package ai.tech.core.misc.auth.server.form

public interface ServerLoginAuth<T : Any> {
    public suspend fun login(username: String, password: String): T
}
