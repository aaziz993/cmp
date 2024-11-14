package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.consul.client.agent.model.Registration
import kotlinx.serialization.Serializable

@Serializable
public data class ConsulConfig(
    val address: String,
    val registration: Registration? = null,
    val config: ConsulConfigConfig? = null,
)
