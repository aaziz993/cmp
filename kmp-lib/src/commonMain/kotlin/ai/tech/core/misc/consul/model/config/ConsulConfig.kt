package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ConsulConfig(
    val address: String,
    val discovery: Discovery? = null,
    val config: Config? = null,
    val loadBalancer: LoadBalancer = LoadBalancer.FIRST_HEALTHY,
    override val enabled: Boolean = true,
) : EnabledConfig
