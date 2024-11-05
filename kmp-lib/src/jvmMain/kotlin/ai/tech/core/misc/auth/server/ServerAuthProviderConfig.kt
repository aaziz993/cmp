package ai.tech.core.misc.auth.server

import ai.tech.core.misc.plugin.session.model.config.CookieConfig

public interface ServerAuthProviderConfig {
    public val cookie: CookieConfig?
}
