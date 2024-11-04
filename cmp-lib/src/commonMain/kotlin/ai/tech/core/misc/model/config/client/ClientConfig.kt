package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.auth.client.model.config.ClientAuthConfig
import ai.tech.core.misc.model.config.SharedConfig
import ai.tech.core.presentation.model.config.SharedPresentationConfig

public interface ClientConfig : SharedConfig {

    public val environment: String
    public val auth: ClientAuthConfig
    public val authProvider: String?
    public val databaseProvider: String?
    public val presentation: SharedPresentationConfig
}
