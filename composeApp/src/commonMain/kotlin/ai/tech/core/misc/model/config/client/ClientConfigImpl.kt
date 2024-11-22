package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.location.localization.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.model.config.ValidatorConfig
import ai.tech.core.misc.model.config.server.ClientHostConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ClientConfigImpl(
    override val application: ApplicationConfig,
    override val httpClient: HttpClientConfig = HttpClientConfig(),
    override val consul: ConsulConfig? = null,
    override val localization: LocalizationConfig,
    override val validator: ValidatorConfig = ValidatorConfig(),
    override val ui: ClientUIConfig,
    override val host: ClientHostConfig
) : ClientConfig
