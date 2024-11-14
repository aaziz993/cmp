package ai.tech.core.misc.config

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.client.kv.model.Value
import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.type.serializer.decodeFromAny
import ai.tech.core.misc.type.serializer.decoderMapFromString
import io.ktor.client.HttpClient
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.reflect.KClass
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

public class ConsulConfigService<T : Config>(
    public val kClass: KClass<T>,
    public val formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    public val readFile: (suspend (path: String) -> String?)? = null,
    public val httpClient: HttpClient? = null,
) {

    private val json: Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    private val decoders = formats.associateWith(::decoder)

    @OptIn(InternalSerializationApi::class, ExperimentalEncodingApi::class)
    @Suppress("UNCHECKED_CAST")
    public suspend fun getConfig(): T = json.decodeFromAny(
        kClass.serializer(),
        mutableMapOf<String, Any?>().apply {
            if(readFile != null) {
                val fileConfig = readFileConfig(APPLICATION_CONFIG_NAME, formats)

                val application: ApplicationConfig = json.decodeFromAny(fileConfig["application"])

                deepMerge(
                    listOf(fileConfig) + application.configurations.map {
                        readFileConfig("${application.environment}/$APPLICATION_CONFIG_NAME-$it", formats)
                    },
                )
            }else{
                emptyMap()
            }

            val consul: ConsulConfig? = fileConfig["consul"]?.let(json::decodeFromAny)

            val consulConfigs = consul?.config?.let {
                val client = ConsulClient(httpClient, consul.address)

                client.readConsulConfig("${it.prefix.trim('/')}/${it.name}.${application.environment}/${it.dataKey.trim('/')}")
            }
        },
    )

    @Suppress("UNCHECKED_CAST")
    private fun decoder(format: String): (String) -> Map<String, Any?> = {
        decoderMapFromString(format)(it) as Map<String, Any?>
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun readFileConfig(path: String, formats: List<String>) =
        deepMerge(
            formats.mapNotNull { format ->
                readFile!!("$path.$format")?.let(decoders[format]!!::invoke)
            },
        )

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun ConsulClient.readConsulConfig(path: String, format: String) =
        kv.getValue(path).mapNotNull(Value::decodedValue).fold(emptyMap<String, Any?>()) { acc, v -> acc + decoders[format]!!(v) }

    private fun deepMerge(configs: List<Map<String, Any?>>) = emptyMap<String, Any?>()

    public companion object {

        public const val APPLICATION_CONFIG_NAME: String = "application"
    }
}
