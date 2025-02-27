package ai.tech.wallet.stock.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.model.config.ScreenConfig
import kotlinx.serialization.Serializable

@Serializable
public data class StockScreenConfig(
    override val route: String = "stock",
    override val auth: AuthResource? = null,
) : ScreenConfig
