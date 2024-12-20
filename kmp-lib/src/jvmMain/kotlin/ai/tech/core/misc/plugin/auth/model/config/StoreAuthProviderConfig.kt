package ai.tech.core.misc.plugin.auth.model.config

public interface StoreAuthProviderConfig {

    public val database: String?
    public val principalTable: String?
    public val roleTable: String?
    public val file: List<String>?
}
