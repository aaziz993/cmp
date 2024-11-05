package ai.tech.core.data.database.keyvalue.room

import ai.tech.core.data.database.keyvalue.room.model.KeyValue
import ai.tech.core.misc.type.TypeResolver
import ai.tech.core.misc.type.decode
import ai.tech.core.misc.type.encode
import ai.tech.core.misc.type.model.Entry
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
public class RoomKeyValue(private val database: KeyValueDatabase) : ai.tech.core.data.keyvalue.KeyValue {

    private val lock: ReentrantLock = reentrantLock()

    private val stateFlow = MutableStateFlow<Entry<String, Any?>>(Entry("", null))

    private val json = Json {
        encodeDefaults = false
        ignoreUnknownKeys = true
    }

    private val dao = database.getDao()

    override suspend fun <T> transactional(block: suspend ai.tech.core.data.keyvalue.KeyValue.() -> T): T {
        roomDb.beginTransaction()

        try {
            // bunch of DAO operations here
            roomDb.setTransactionSuccessful()
        }
        finally {
            roomDb.endTransaction()
        }
    }

    override suspend fun contains(keys: List<String>): Boolean = dao.exists(keys.toKey())

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T> set(keys: List<String>, value: T): Unit = lock.withLock {
        keys.toKey().let { key ->
            dao.insert(KeyValue(key = key, value = value?.let { json.encode(it, TypeResolver(it::class)) }))
            stateFlow.update { Entry(key, value) }
        }
    }

    override suspend fun <T> get(
        keys: List<String>,
        type: TypeResolver,
        defaultValue: T?
    ): T = keys.toKey().let {
        (dao.select(it)?.value?.let { json.decode(it, type) } ?: defaultValue) as T
    }

    override suspend fun <T> getFlow(
        keys: List<String>,
        type: TypeResolver,
    ): Flow<T> = keys.toKey().let { key -> stateFlow.filter { it.key == key }.map { it.value } as Flow<T> }

    override suspend fun remove(keys: List<String>): Unit =
        keys.toKey().let {
            dao.deleteByKeyLike("$it$KEY_DELIMITER%")
        }

    override suspend fun clear(): Unit = dao.deleteAll()

    override suspend fun flush(): Unit = Unit

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$KEY_DELIMITER$v}" }

    public companion object {

        public const val KEY_DELIMITER: Char = '.'
    }
}
