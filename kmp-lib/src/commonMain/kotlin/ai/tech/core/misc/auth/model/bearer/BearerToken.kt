package ai.tech.core.misc.auth.model.bearer

public interface BearerToken {

    public val token: String
    public val refreshToken: String
}
