package ai.tech.core.misc.model.config.server

public interface ServerSSLConfig {

    public val keyStore: String
    public val keyStorePassword: String
    public val keyAlias: String
    public val privateKeyPassword: String
}
