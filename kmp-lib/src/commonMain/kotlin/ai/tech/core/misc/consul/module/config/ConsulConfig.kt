package ai.tech.core.misc.consul.module.config;

import kotlinx.serialization.Serializable

@Serializable
public data class ConsulConfig(
    val address: String,
    val service: ConsulServiceConfig? = null
)
