package ai.tech.core.misc.config

import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.type.decodeFromAny
import ai.tech.core.misc.type.copyTo
import kotlin.reflect.KClass
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.pearx.kasechange.toCamelCase

public abstract class AbstractConfigService<T : Config>(public val kClass: KClass<T>) {

    protected val json: Json = Json {
        ignoreUnknownKeys = true
    }

    protected abstract suspend fun readConfigs(): List<Map<String, Any?>>

    @OptIn(InternalSerializationApi::class)
    public suspend fun getConfig(): T = json.decodeFromAny(
        kClass.serializer(),
        mutableMapOf<String, Any?>().apply {
            readConfigs().forEach {
                it.copyTo(
                    this, { _, key -> key.toString().toCamelCase() },
                    { _, _, _ ->

                    },
                )
            }
        },
    )
}
