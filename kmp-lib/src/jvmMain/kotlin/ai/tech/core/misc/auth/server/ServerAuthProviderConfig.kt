package ai.tech.core.misc.auth.server

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig

public interface ServerAuthProviderConfig : EnabledConfig {
    public val cookie: CookieConfig?
}
