package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ConsulConfig(
    val address: String,
    val registration: Registration? = null,
    val config: ConsulConfigConfig? = null,
    val loadBalancer: LoadBalancer = LoadBalancer.FIRST_HEALTHY,
    override val enable: Boolean = true,
):EnabledConfig
