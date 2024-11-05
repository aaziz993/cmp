package ai.tech.wallet.crypto.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.model.config.presentation.DestinationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CryptoScreenConfig(
    override val route: String,
    override val auth: AuthResource? = null,
) : DestinationConfig
