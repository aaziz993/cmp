package ai.tech.core.misc.plugin.cors.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class CORSHostConfig(
    val host: String,
    val schemes: List<String> = listOf("http"),
    val subDomains: List<String> = emptyList()
)
