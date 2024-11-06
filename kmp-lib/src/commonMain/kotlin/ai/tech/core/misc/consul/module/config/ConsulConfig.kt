package ai.tech.core.misc.consul.module.config;

import kotlinx.serialization.Serializable

@Serializable
public data class ConsulConfig(
    var address: String
)
