package ai.tech.wallet.balance.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.model.config.presentation.DestinationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class BalanceScreenConfig(
    override val route: String,
    override val auth: AuthResource? = null,
) : DestinationConfig