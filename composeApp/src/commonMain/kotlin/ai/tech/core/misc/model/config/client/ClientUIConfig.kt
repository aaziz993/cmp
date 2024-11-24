package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.auth.client.model.config.ClientAuthConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.presentation.model.config.ClientPresentationConfigImpl
import kotlinx.serialization.Serializable

@Serializable
public data class ClientUIConfig(
    public val application: ApplicationConfig,
    override val auth: ClientAuthConfig,
    override val databaseName: String? = null,
    override val presentation: ClientPresentationConfigImpl
) : SharedUIConfig
