package ai.tech.core.misc.model.config.server

import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.location.localization.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.model.config.LogConfig
import ai.tech.core.misc.model.config.client.ServerUIConfig
import ai.tech.core.misc.model.config.ValidatorConfig
import ai.tech.core.misc.model.config.client.HttpClientConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ServerConfigImpl(
    override val log: LogConfig = LogConfig(),
    override val httpClient: HttpClientConfig = HttpClientConfig(),
    override val consul: ConsulConfig? = null,
    override val localization: LocalizationConfig = LocalizationConfig(),
    override val validator: ValidatorConfig = ValidatorConfig(),
    override val ui: ServerUIConfig,
    override val host: ServerHostConfig,
) : ServerConfig
