package ai.tech.core.data.keyvalue.kstore

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.data.keyvalue.SettingsKeyValue.Companion.KEY_DELIMITER
import ai.tech.core.data.keyvalue.kstore.model.KeyValue
import ai.tech.core.misc.type.TypeResolver
import ai.tech.core.misc.type.callOrNull
import ai.tech.core.misc.type.containsOrNull
import ai.tech.core.misc.type.decode
import ai.tech.core.misc.type.encode
import ai.tech.core.misc.type.model.Entry
import ai.tech.core.misc.type.removeOrNull
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storeOf
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.io.files.Path
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
public open class KStoreKeyValue(
    private val store: KStore<List<Entry<String, String>>>,
) : AbstractKeyValue() {

    override suspend fun contains(keys: List<String>): Boolean = keys.toKey().let { key ->
        store.get()?.any { it.key == key } == true
    }

    override suspend fun <T> set(keys: List<String>, value: T): Unit = keys.toKey().let {
        val entry = Entry(it, json.encode(value, TypeResolver(value::class)))
        store.set(store.get().orEmpty() + entry)
    }

    override suspend fun <T> get(
        keys: List<String>,
        type: TypeResolver,
        defaultValue: T?
    ): T = keys.toKey().let { key ->
        (store.get()?.find { it.key == key }?.let {
            json.decode(it.value, type)
        } ?: defaultValue) as T
    }

    override suspend fun <T> getFlow(
        keys: List<String>,
        type: TypeResolver,
    ): Flow<T> = keys.toKey().let { key->
        store.updates.map {
            it?.find { it.key == key  }
        }
    }

    public override suspend fun remove(keys: List<String>) {
        store.update {

        }
    }

    override suspend fun clear(): Unit = map.clear()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Int = map.size

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$KEY_DELIMITER$v" }
}
