package ai.tech.core.misc.auth.rbac.model

public class RBACPluginConfig {
    public var roleExtractor: ((Any) -> Set<String>) = { emptySet() }

    public var throwException: Boolean? = null
}
