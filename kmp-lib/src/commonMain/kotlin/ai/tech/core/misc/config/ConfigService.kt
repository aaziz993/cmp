package ai.tech.core.misc.config

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.client.kv.model.Value
import ai.tech.core.misc.consul.client.model.KVMetadata
import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.type.decodeFromAny
import ai.tech.core.misc.type.copyTo
import ai.tech.core.misc.type.decodeAnyFromString
import ai.tech.core.misc.type.multiple.decode
import ai.tech.core.misc.type.multiple.decodeBase64
import io.ktor.client.HttpClient
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.reflect.KClass
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import net.pearx.kasechange.toCamelCase

public class ConfigService<T : Config>(
    public val kClass: KClass<T>,
    public val readFile: suspend (path: String) -> String?,
    public val httpClient: HttpClient,
) {

    protected val json: Json = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(InternalSerializationApi::class, ExperimentalEncodingApi::class)
    @Suppress("UNCHECKED_CAST")
    public suspend fun getConfig(): T = json.decodeFromAny(
        kClass.serializer(),
        mutableMapOf<String, Any?>().apply {
            val bootstrap = json.decodeAnyFromString(JsonObject::class.serializer(), readFile(BOOTSTRAP_CONFIG_FILE)!!) as Map<String, Any?>

            val environment = bootstrap["environment"] as String

            val configurations = bootstrap["configurations"] as List<String>

            val consulClient = bootstrap["consul"]?.let { ConsulClient(httpClient, json.decodeFromAny(it)) }

            listOf(
                SHARED_CONFIG_NAME,
                APPLICATION_CONFIG_NAME,
            ).flatMap {
                (listOf(it) + configurations.map { "$environment$it" })
            }.flatMap {
                listOf(readFileConfig(it), consulClient?.readConsulConfig(it))
            }.filterNotNull().forEach {
                it.copyTo(
                    this, { _, key -> key.toString().toCamelCase() },
                    { _, _, _, _ ->

                    },
                )
            }
        },
    )

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun readFileConfig(path: String) = readFile(path)?.let { Json.Default.decodeAnyFromString(JsonObject::class.serializer(), it) } as Map<String, Any?>?

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun ConsulClient.readConsulConfig(path: String) = kv.getValue(path).map(Value::value).fold(emptyMap<String, Any?>()) { acc, v -> json.decodeAnyFromString(JsonObject::class.serializer(), v!!.decodedValue) as Map<String, Any?> }

    public companion object {

        public const val BOOTSTRAP_CONFIG_FILE: String = "application-shared"
        public const val SHARED_CONFIG_NAME: String = "application-shared"
        public const val APPLICATION_CONFIG_NAME: String = "application"
    }
}
