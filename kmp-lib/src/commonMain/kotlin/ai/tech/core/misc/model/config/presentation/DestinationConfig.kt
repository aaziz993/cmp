package ai.tech.core.misc.model.config.presentation

import ai.tech.core.misc.auth.model.AuthResource

public interface DestinationConfig {
    public val route: String
    public val auth: AuthResource?
}
