package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.model.config.server.ClientHostConfig

public interface ClientConfig : Config {

    override val host: ClientHostConfig
}
