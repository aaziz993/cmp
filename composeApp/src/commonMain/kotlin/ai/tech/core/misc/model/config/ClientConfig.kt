package ai.tech.core.misc.model.config

import ai.tech.core.misc.auth.client.model.config.ClientAuthConfig
import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.location.localization.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.client.ClientConfig
import ai.tech.core.misc.model.config.server.KtorServerConfig
import ai.tech.core.presentation.model.config.ClientPresentationConfigImpl
import kotlinx.serialization.Serializable

@Serializable
public data class ClientConfig(
    override val project: String,
    override val consul: ConsulConfig,
    override val localization: LocalizationConfig,
    override val databaseProvider: String,
    override val validator: ValidatorConfig = ValidatorConfig(),
    override val ktor: KtorServerConfig,
    override val auth: ClientAuthConfig,
    override val presentation: ClientPresentationConfigImpl,
) : ClientConfig
