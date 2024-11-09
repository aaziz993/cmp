package ai.tech.core.misc.plugin.auth.model.config

public interface DatabaseAuthConfig : AuthProviderConfig {

    public val database: String
    public val userTable: String
    public val roleTable: String?
}
