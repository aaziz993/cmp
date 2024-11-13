package ai.tech.core.misc.model.config

import ai.tech.core.misc.consul.client.model.config.ConsulConfig
import ai.tech.core.misc.location.localization.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.client.KtorClientConfig
import ai.tech.core.misc.model.config.server.KtorServerConfig

public interface Config {

    public val project: String

    public val ktorClient: KtorClientConfig

    public val consul: ConsulConfig?

    public val localization: LocalizationConfig

    public val validator: ValidatorConfig

    public val ktor: KtorServerConfig
}
