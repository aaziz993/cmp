package ai.tech.core.misc.plugin.cors.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CORSHostConfig(
    val host: String,
    val schemes: List<String> = listOf("http"),
    val subDomains: List<String> = emptyList(),
    override val enable: Boolean = true,
) : EnabledConfig
