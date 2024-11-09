package ai.tech.core.misc.plugin.auth.model.config

public interface DatabaseAuthConfig : AuthProviderConfig {

    public val database: String
    public val principalTable: String
    public val roleTable: String?
}
