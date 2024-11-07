package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.auth.client.model.config.ClientAuthConfig
import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.model.config.presentation.PresentationConfig

public interface ClientConfig : Config {

    public val database: String
    public val auth: ClientAuthConfig
    public val presentation: PresentationConfig
}
