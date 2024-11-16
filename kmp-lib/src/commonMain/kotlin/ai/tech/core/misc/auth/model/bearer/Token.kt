package ai.tech.core.misc.auth.model.bearer

public interface Token {

    public val accessToken: String
    public val expiresIn: Int
    public val refreshToken: String?
    public val refreshExpiresIn: Int?
    public val scope: String
    public val tokenType: String
    public val idToken: String?
}
