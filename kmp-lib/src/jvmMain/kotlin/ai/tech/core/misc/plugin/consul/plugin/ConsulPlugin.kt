package ai.tech.core.misc.plugin.consul.plugin

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.plugin.consul.plugin.model.config.ConsulPluginConfig
import io.ktor.client.HttpClient
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.createApplicationPlugin
import kotlinx.coroutines.runBlocking

@Suppress("FunctionName")
public fun ConsulMicroservice(
    httpClient: HttpClient,
    config: ConsulConfig,
): ApplicationPlugin<ConsulPluginConfig> = createApplicationPlugin(
    "ConsulPlugin",
    { ConsulPluginConfig(config) },
) {
    val consulClient = ConsulClient(httpClient, pluginConfig.config.address)

    pluginConfig.service?.let {
        runBlocking {
            consulClient.agent.register(it)
        }
    }
}
