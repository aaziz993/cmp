package ai.tech.core.misc.plugin.consul.plugin

import ai.tech.core.misc.consul.client.model.config.ConsulConfig
import ai.tech.core.misc.plugin.consul.plugin.model.config.ConsulPluginConfig
import io.ktor.client.HttpClient
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.createApplicationPlugin

@Suppress("FunctionName")
public fun ConsulMicroservice(
    httpClient: HttpClient,
    config: ConsulConfig,
): ApplicationPlugin<ConsulPluginConfig> = createApplicationPlugin(
    "ConsulPlugin",
    { ConsulPluginConfig(config) },
) {
//    val consul = Consul(httpClient, pluginConfig.config)
//
//    pluginConfig.service?.let {
//        runBlocking {
//            consul.agent.register(
//                it.name,
//                it.id,
//                it.tags,
//                it.address,
//                it.taggedAddress,
//                it.meta,
//                it.port,
//                it.kind,
//                it.proxy,
//                it.connect,
//                it.check,
//                it.enableTagOverride,
//                it.weights,
//                it.replaceExistingChecks,
//            )
//        }
//    }
}
