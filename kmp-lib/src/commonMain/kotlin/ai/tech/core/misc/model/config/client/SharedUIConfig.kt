package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.auth.client.model.config.ClientAuthConfig
import ai.tech.core.presentation.model.config.SharedPresentationConfig

public interface SharedUIConfig {

    public val auth: ClientAuthConfig
    public val databaseName: String
    public val presentation: SharedPresentationConfig
}
