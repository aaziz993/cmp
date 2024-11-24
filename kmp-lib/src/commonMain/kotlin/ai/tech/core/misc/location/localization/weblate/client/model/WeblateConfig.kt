package ai.tech.core.misc.location.localization.weblate.client.model

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class WeblateConfig(
    val address: String,
    val apiKey: String,
    override val enabled: Boolean = true,
) : EnabledConfig
