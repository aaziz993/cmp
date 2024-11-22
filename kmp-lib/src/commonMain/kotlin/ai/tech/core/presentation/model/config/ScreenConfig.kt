package ai.tech.core.presentation.model.config

import ai.tech.core.misc.auth.model.AuthResource

public interface ScreenConfig {
    public val route: String
    public val auth: AuthResource?
}
