@file:OptIn(ExperimentalSerializationApi::class)

package ai.tech.core.data.keyvalue

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import com.russhwolf.settings.coroutines.getBooleanOrNullFlow
import com.russhwolf.settings.coroutines.getDoubleOrNullFlow
import com.russhwolf.settings.coroutines.getFloatOrNullFlow
import com.russhwolf.settings.coroutines.getIntOrNullFlow
import com.russhwolf.settings.coroutines.getLongOrNullFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.capturedKClass
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalSettingsApi::class)
public class SettingsKeyValue(
    public val keyDelimiter: Char = '.',
    public val nullValue: String = "null",
) : AbstractKeyValue() {

    private val settings: Settings by lazy { Settings() }

    private val observableSettings: ObservableSettings by lazy { settings as ObservableSettings }

    override suspend fun contains(keys: List<String>): Boolean = settings.contains(keys.toKey())

    override suspend fun <T> set(keys: List<String>, value: T): Unit = keys.toKey().let { key ->
        when (value) {
            is Boolean -> settings.putBoolean(key, value)
            is Int -> settings.putInt(key, value)
            is Long -> settings.putLong(key, value)
            is Float -> settings.putFloat(key, value)
            is Double -> settings.putDouble(key, value)
            is String -> settings.putString(key, value)
            else -> settings.putString(key, value?.let(json::encodeToString) ?: nullValue)
        }
    }

    override suspend fun <T> get(keys: List<String>, deserializer: DeserializationStrategy<T>, defaultValue: T?): T = keys.toKey().let {
        (when (deserializer.descriptor.capturedKClass) {
            Boolean::class -> settings.getBooleanOrNull(it)
            Int::class -> settings.getIntOrNull(it)
            Long::class -> settings.getLongOrNull(it)
            Float::class -> settings.getFloatOrNull(it)
            Double::class -> settings.getDoubleOrNull(it)
            String::class -> settings.getStringOrNull(it)
            else -> settings.getStringOrNull(it)?.let {
                if (it == nullValue) {
                    null
                }
                else {
                    json.decodeFromString(deserializer, it)
                }
            }
        } ?: defaultValue) as T
    }

    override suspend fun <T> getFlow(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
    ): Flow<T> = keys.toKey().let {
        when (deserializer.descriptor.capturedKClass) {
            Boolean::class -> observableSettings.getBooleanOrNullFlow(it)
            Int::class -> observableSettings.getIntOrNullFlow(it)
            Long::class -> observableSettings.getLongOrNullFlow(it)
            Float::class -> observableSettings.getFloatOrNullFlow(it)
            Double::class -> observableSettings.getDoubleOrNullFlow(it)
            String::class -> observableSettings.getStringOrNullFlow(it)
            else -> observableSettings.getStringOrNullFlow(it).map {
                it?.let {
                    if (it == "null") {
                        null
                    }
                    else {
                        json.decodeFromString(deserializer, it)
                    }
                }
            }
        }.map { it as T }
    }

    override suspend fun remove(keys: List<String>): Unit = lock.withLock {
        keys.toKey().let { key ->
            val subKey = "$key$keyDelimiter"

            settings.keys.filter { it == key || it.startsWith(subKey) }.forEach {
                settings.remove(it)
            }
        }
    }

    override suspend fun clear(): Unit = settings.clear()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Long = settings.size.toLong()

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$keyDelimiter$v" }
}
