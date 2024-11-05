package ai.tech.core.data.keyvalue

import ai.tech.core.misc.type.TypeResolver
import ai.tech.core.misc.type.decode
import ai.tech.core.misc.type.encode
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
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalSettingsApi::class)
public class SettingsKeyValue : KeyValue {

    private val lock: ReentrantLock = reentrantLock()

    private val settings: Settings by lazy { Settings() }

    private val observableSettings: ObservableSettings by lazy { settings as ObservableSettings }

    private val json = Json {
        encodeDefaults = false
        ignoreUnknownKeys = true
    }

    override suspend fun <T> transactional(block: suspend KeyValue.() -> T): T = lock.withLock {
        block()
    }

    override suspend fun contains(keys: List<String>): Boolean = settings.contains(keys.toKey())

    override suspend fun <T> set(keys: List<String>, value: T): Unit = keys.toKey().let { ks ->
        when (value) {
            is Boolean -> settings.putBoolean(ks, value)
            is Int -> settings.putInt(ks, value)
            is Long -> settings.putLong(ks, value)
            is Float -> settings.putFloat(ks, value)
            is Double -> settings.putDouble(ks, value)
            is String -> settings.putString(ks, value)
            else -> value?.let { settings.putString(ks, json.encode(it, TypeResolver(it::class))) }
                ?: settings.remove(ks)
        }
    }

    override suspend fun <T> get(
        keys: List<String>,
        type: TypeResolver,
        defaultValue: T?
    ): T = keys.toKey().let {
        (when (type.kClass) {
            Boolean::class -> settings.getBooleanOrNull(it)
            Int::class -> settings.getIntOrNull(it)
            Long::class -> settings.getLongOrNull(it)
            Float::class -> settings.getFloatOrNull(it)
            Double::class -> settings.getDoubleOrNull(it)
            String::class -> settings.getStringOrNull(it)
            else -> settings.getStringOrNull(it)?.let { json.decode(it, type) }
        } ?: defaultValue) as T
    }

    override suspend fun <T> getFlow(
        keys: List<String>,
        type: TypeResolver,
    ): Flow<T> = keys.toKey().let {
        when (type.kClass) {
            Boolean::class -> observableSettings.getBooleanOrNullFlow(it)
            Int::class -> observableSettings.getIntOrNullFlow(it)
            Long::class -> observableSettings.getLongOrNullFlow(it)
            Float::class -> observableSettings.getFloatOrNullFlow(it)
            Double::class -> observableSettings.getDoubleOrNullFlow(it)
            String::class -> observableSettings.getStringOrNullFlow(it)
            else -> observableSettings.getStringOrNullFlow(it)
                .map { it?.let { json.decode(it, type) } }
        } as Flow<T>
    }

    override suspend fun remove(keys: List<String>): Unit = lock.withLock {
        keys.toKey().let { "$it$KEY_DELIMITER" }.let { ks ->
            settings.keys.filter { it.startsWith(ks) }.let {
                it.forEach {
                    settings.remove(it)
                }
                it.isNotEmpty()
            }
        }
    }

    override suspend fun clear(): Unit = settings.clear()

    override suspend fun flush(): Unit = Unit

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$KEY_DELIMITER$v" }

    public companion object {

        public const val KEY_DELIMITER: Char = '.'
    }
}
