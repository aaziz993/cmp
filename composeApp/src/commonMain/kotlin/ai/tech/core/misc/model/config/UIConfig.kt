package ai.tech.core.misc.model.config

import ai.tech.core.misc.auth.client.model.config.ClientAuthConfig
import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.location.localization.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.client.UIConfig
import ai.tech.core.misc.model.config.client.HttpClientConfig
import ai.tech.core.misc.model.config.server.HostConfig
import ai.tech.core.presentation.model.config.ClientPresentationConfigImpl
import kotlinx.serialization.Serializable

@Serializable
public data class UIConfig(
    override val application: ApplicationConfig,
    override val httpClient: HttpClientConfig = HttpClientConfig(),
    override val consul: ConsulConfig? = null,
    override val localization: LocalizationConfig,
    override val database: String,
    override val validator: ValidatorConfig = ValidatorConfig(),
    override val auth: ClientAuthConfig,
    override val presentation: ClientPresentationConfigImpl,
    override val host: HostConfig
) : UIConfig
