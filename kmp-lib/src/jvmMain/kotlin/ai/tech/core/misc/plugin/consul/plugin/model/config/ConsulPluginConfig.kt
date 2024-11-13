package ai.tech.core.misc.plugin.consul.plugin.model.config

import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.consul.module.config.ConsulPluginServiceConfig

public class ConsulPluginConfig(public val config: ConsulConfig) {

    internal var service: ConsulPluginServiceConfig? = null

    public fun service(block: ConsulPluginServiceConfig.() -> Unit) {
        service = ConsulPluginServiceConfig().apply {
            block()
        }
    }
}
