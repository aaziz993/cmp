package ai.tech.core.misc.auth.server.rbac.model

public class ServerRBACPluginConfig {
    public var roleExtractor: ((Any) -> Set<String>) = { emptySet() }

    public var throwException: Boolean? = null
}
