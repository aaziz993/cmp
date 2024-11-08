package ai.tech.core.data.keyvalue

import ai.tech.core.KeyValueQueries
import ai.tech.core.data.database.sqldelight.KeyValue
import ai.tech.core.misc.type.TypeResolver
import ai.tech.core.misc.type.decode
import ai.tech.core.misc.type.encode
import ai.tech.core.misc.type.model.Entry
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.InternalSerializationApi

@Suppress("UNCHECKED_CAST")
public class SqlDelightKeyValue(private val queries: KeyValueQueries, public val keyDelimiter: Char = '.') : AbstractKeyValue() {

    private val stateFlow = MutableStateFlow<Entry<String, Any?>>(Entry("", null))

    override suspend fun <T> transactional(block: suspend ai.tech.core.data.keyvalue.AbstractKeyValue.() -> T): T =
        super.transactional {
            queries.transactionWithResult(false) {
                block()
            }
        }

    override suspend fun contains(keys: List<String>): Boolean = queries.exists(keys.toKey()).executeAsOne()

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T> set(keys: List<String>, value: T): Unit = lock.withLock {
        keys.toKey().let { key ->
            queries.insert(key, value?.let { json.encode(it, TypeResolver(it::class)) })
            stateFlow.update { Entry(key, value) }
        }
    }

    override suspend fun <T> get(
        keys: List<String>,
        type: TypeResolver,
        defaultValue: T?
    ): T = keys.toKey().let {
        (queries.select(it).executeAsOne().value_?.let {
            json.decode(it, type)
        } ?: defaultValue) as T
    }

    override suspend fun <T> getFlow(
        keys: List<String>,
        type: TypeResolver,
    ): Flow<T> = keys.toKey().let { key -> stateFlow.filter { it.key == key }.map { it.value } as Flow<T> }

    override suspend fun remove(keys: List<String>): Unit = transactional {
        keys.toKey().let {
            queries.delete(it)
            queries.deleteLike("$it$keyDelimiter%")
        }
    }

    override suspend fun clear(): Unit = queries.deleteAll()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Int = queries.count()

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$keyDelimiter$v}" }
}
