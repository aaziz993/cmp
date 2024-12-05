package ai.tech.core.misc.auth.client.bearer.model

public interface BearerToken {

    public val token: String
    public val refreshToken: String?
        get() = null
}
