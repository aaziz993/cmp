package ai.tech.core.misc.consul.model.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class ConsulConfigConfig(
    val format: String = "YAML",
    val aclToken: String,
    val defaultContext: String = "application",
    val prefix: String = "config/",
    val name: String = "application",
    val dataKey: String = "data/properties"
)
