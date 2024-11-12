package ai.tech.core.misc.config

import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.type.decodeFromAny
import kotlin.reflect.KClass
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

public class FileConfigService<T : Config>(kClass: KClass<T>) : AbstractConfigService<T>(kClass) {

    override suspend fun readConfigs(): List<Map<String, Any?>> {
        TODO("Not yet implemented")
    }

    public companion object {

        public const val APPLICATION_SHARED_CONFIG_NAME: String = "application-shared.yml"
        public const val APPLICATION_CONFIG_NAME: String = "application.yml"
    }
}
