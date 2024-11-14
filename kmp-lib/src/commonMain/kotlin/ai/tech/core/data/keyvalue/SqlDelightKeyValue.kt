package ai.tech.core.data.keyvalue

import ai.tech.core.KeyValueQueries
import ai.tech.core.misc.type.model.Entry
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString

@Suppress("UNCHECKED_CAST")
public class SqlDelightKeyValue(private val queries: KeyValueQueries, public val keyDelimiter: Char = '.') : AbstractKeyValue() {

    private val stateFlow = MutableStateFlow<Entry<String, Any?>>(Entry("", null))

    override suspend fun <T> transactional(block: suspend AbstractKeyValue.() -> T): T =
        super.transactional {
            queries.transactionWithResult(false) {
                block()
            }
        }

    override suspend fun contains(keys: List<String>): Boolean = queries.exists(keys.toKey()).executeAsOne()

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T> set(keys: List<String>, value: T): Unit = lock.withLock {
        keys.toKey().let { key ->
            queries.insert(key, value?.let { json.encodeToString(it) })
            stateFlow.update { Entry(key, value) }
        }
    }

    override suspend fun <T> get(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
        defaultValue: T?
    ): T = keys.toKey().let {
        (queries.select(it).executeAsOne().value_?.let { json.decodeFromString(deserializer, it) } ?: defaultValue) as T
    }

    override suspend fun <T> getFlow(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
    ): Flow<T> = keys.toKey().let { key -> stateFlow.filter { it.key == key }.map { it.value as T } }

    override suspend fun remove(keys: List<String>): Unit = transactional {
        keys.toKey().let {
            queries.delete(it)
            queries.deleteLike("$it$keyDelimiter%")
        }
    }

    override suspend fun clear(): Unit = queries.deleteAll()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Long = queries.count().executeAsOne()

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$keyDelimiter$v}" }
}
