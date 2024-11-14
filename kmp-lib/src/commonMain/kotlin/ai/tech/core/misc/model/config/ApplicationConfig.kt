package ai.tech.core.misc.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class ApplicationConfig(
    val name: String,
    val environment: String = "development",
    val configurations: List<String> = emptyList(),
)
