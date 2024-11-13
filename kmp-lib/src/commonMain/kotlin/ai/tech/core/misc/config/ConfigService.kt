package ai.tech.core.misc.config

import ai.tech.core.misc.consul.Consul
import ai.tech.core.misc.consul.module.KVMetadata
import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.type.decodeFromAny
import ai.tech.core.misc.type.copyTo
import ai.tech.core.misc.type.decodeAnyFromString
import io.ktor.client.HttpClient
import kotlin.reflect.KClass
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import net.pearx.kasechange.toCamelCase

public class ConfigService<T : Config>(
    public val kClass: KClass<T>,
    public val readFile: suspend (path: String) -> String,
    public val httpClient: HttpClient,
) {

    protected val json: Json = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    public suspend fun getConfig(): T = json.decodeFromAny(
        kClass.serializer(),
        mutableMapOf<String, Any?>().apply {
            val bootstrap = json.decodeAnyFromString(JsonObject::class.serializer(), readFile(BOOTSTRAP_CONFIG_FILE)) as Map<String, Any?>

            val consul = Consul(httpClient, json.decodeFromAny(bootstrap["consul"]))

            consul.kv.read(APPLICATION_CONFIG_NAME).orEmpty().map(KVMetadata::value).fold(emptyMap<String, Any?>()) { acc, v -> json.decodeAnyFromString(JsonObject::class.serializer(), v) as Map<String, Any?> }

            readConfigs().forEach {
                it.copyTo(
                    this, { _, key -> key.toString().toCamelCase() },
                    { _, _, _, _ ->

                    },
                )
            }
        },
    )

    public companion object {

        public const val BOOTSTRAP_CONFIG_FILE: String = "application-shared"
        public const val SHARED_CONFIG_NAME: String = "application-shared"
        public const val APPLICATION_CONFIG_NAME: String = "application"
    }
}
