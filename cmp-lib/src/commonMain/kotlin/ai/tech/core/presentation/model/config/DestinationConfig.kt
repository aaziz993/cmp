package ai.tech.core.presentation.model.config

import ai.tech.core.misc.auth.model.AuthResource

public interface DestinationConfig {
    public val route: String
    public val auth: AuthResource?
}
