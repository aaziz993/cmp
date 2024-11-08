package ai.tech.core.data.keyvalue.kstore

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.type.model.Entry
import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString

@Suppress("UNCHECKED_CAST")
public open class KStoreKeyValue(
    private val store: KStore<List<Entry<String, String?>>>,
    public val keyDelimiter: Char = '.'
) : AbstractKeyValue() {

    override suspend fun contains(keys: List<String>): Boolean = keys.toKey().let { key ->
        store.get()?.any { it.key == key } == true
    }

    override suspend fun <T> set(keys: List<String>, value: T): Unit = keys.toKey().let { key ->
        val entry = Entry(key, value?.let { json.encodeToString(it) })
        store.set(store.get()?.filter { it.key != key }.orEmpty() + entry)
    }

    override suspend fun <T> get(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
        defaultValue: T?
    ): T = keys.toKey().let { key ->
        (store.get()?.find { it.key == key }?.value?.let { json.decodeFromString(deserializer, it) } ?: defaultValue) as T
    }

    override suspend fun <T> getFlow(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
    ): Flow<T> = keys.toKey().let { key ->
        store.updates.map { it?.find { it.key == key } }.filterNotNull().map { (_, v) -> v?.let { json.decodeFromString(deserializer, it) } as T }
    }

    public override suspend fun remove(keys: List<String>): Unit = keys.toKey().let { key ->
        val subKey = "$key$keyDelimiter"

        store.set(store.get()?.filter { it.key == key || it.key.startsWith(subKey) })
    }

    override suspend fun clear(): Unit = store.delete()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Int = store.get()?.size ?: 0

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$keyDelimiter$v" }
}
