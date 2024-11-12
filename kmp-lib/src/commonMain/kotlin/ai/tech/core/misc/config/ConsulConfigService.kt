package ai.tech.core.misc.config

import ai.tech.core.misc.consul.Consul
import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.type.decodeAnyFromJsonElement
import io.ktor.client.HttpClient
import kotlin.reflect.KClass

public class ConsulConfigService<T : Config>(
    kClass: KClass<T>,
    httpClient: HttpClient,
    public val config: ConsulConfig,
) : AbstractConfigService<T>(kClass) {

    public val consul: Consul = Consul(httpClient, config)

    override suspend fun readConfigs(): List<Map<String, Any?>> = listOf(
        json.decodeAnyFromJsonElement(consul.config.read("yaml", APPLICATION_CONFIG_KEY)) as Map<String, Any?>,
    )

    public companion object {

        public const val APPLICATION_CONFIG_KEY: String = "application"
    }
}
