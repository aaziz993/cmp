package ai.tech.core.misc.model.config.server

import ai.tech.core.misc.model.config.Config

public interface ServerConfig : Config {

    override val host: HostDeploymentConfig
}

