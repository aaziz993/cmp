package ai.tech.core.data.location.weblate.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateConfig(
    val address: String,
    val apiKey: String,
//    override val enable: Boolean? = null,
)
//    : EnablableConfig