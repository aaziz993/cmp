package ai.tech.core.misc.model.config

import ai.tech.core.misc.auth.client.model.config.ClientAuthConfig
import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.location.localization.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.client.ClientConfig
import ai.tech.core.misc.model.config.client.KtorClientConfig
import ai.tech.core.presentation.model.config.ClientPresentationConfigImpl
import kotlinx.serialization.Serializable

@Serializable
public data class ClientConfig(
    override val application: ApplicationConfig,
    override val ktorClient: KtorClientConfig = KtorClientConfig(),
    override val consul: ConsulConfig? = null,
    override val localization: LocalizationConfig,
    override val database: String,
    override val validator: ValidatorConfig = ValidatorConfig(),
    override val ktor: KtorServerConfig,
    override val auth: ClientAuthConfig,
    override val presentation: ClientPresentationConfigImpl,
) : ClientConfig
