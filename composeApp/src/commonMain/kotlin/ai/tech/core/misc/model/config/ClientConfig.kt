package ai.tech.core.misc.model.config

import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import ai.tech.core.misc.auth.client.model.config.ClientAuthConfig
import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.config.LocalizationConfig
import ai.tech.core.misc.location.model.languages
import ai.tech.core.misc.model.config.ValidatorConfig
import ai.tech.core.misc.model.config.client.ClientConfig
import ai.tech.core.misc.model.config.server.KtorServerConfig
import ai.tech.core.presentation.model.config.ClientPresentationConfig
import ai.tech.core.presentation.model.config.ClientPresentationConfigImpl
import kotlinx.serialization.Serializable

@Serializable
public data class ClientConfig(
    override val project: String,
    override val consul: ConsulConfig,
    override val ktor: KtorServerConfig,
    override val localization: LocalizationConfig,
    override val localizationProvider: String? = null,
    override val language: String = "eng-US",
    override val validator: ValidatorConfig = ValidatorConfig(),
    override val auth: ClientAuthConfig,
    override val authProvider: String? = null,
    override val database: Map<String, DatabaseProviderConfig>?,
    override val databaseProvider: String?,
    override val presentation: ClientPresentationConfigImpl,
) : ClientConfig
