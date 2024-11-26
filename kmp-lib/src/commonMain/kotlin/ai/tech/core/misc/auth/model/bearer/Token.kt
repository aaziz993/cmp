package ai.tech.core.misc.auth.model.bearer

public interface Token {

    public val idToken: String?
    public val refreshToken: String
}
