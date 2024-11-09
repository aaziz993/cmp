package ai.tech.core.misc.plugin.auth.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig

public interface AuthProviderConfig : EnabledConfig {
    public val cookie: CookieConfig?
}
