package ai.tech.core.data.database.keyvalue.room

import ai.tech.core.data.database.keyvalue.room.model.KeyValue
import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.type.model.Entry
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString

@Suppress("UNCHECKED_CAST")
public class RoomKeyValue(private val database: KeyValueDatabase) : AbstractKeyValue() {

    private val dao = database.getDao()

    private val stateFlow = MutableStateFlow<Entry<String, Any?>>(Entry("", null))

    override suspend fun <T> transactional(block: suspend AbstractKeyValue.() -> T): T =
        super.transactional {
//            database.withTransaction {
            block()
//            }
        }

    override suspend fun contains(keys: List<String>): Boolean = dao.exists(keys.toKey())

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T> set(keys: List<String>, value: T): Unit = lock.withLock {
        keys.toKey().let { key ->
            dao.insert(KeyValue(key = key, value = value?.let { json.encodeToString(it) }))
            stateFlow.update { Entry(key, value) }
        }
    }

    override suspend fun <T> get(
        keys: List<String>,
        serializer: KSerializer<T>,
        defaultValue: T?
    ): T = keys.toKey().let {
        (dao.select(it)?.value?.let { json.decodeFromString(serializer,it) } ?: defaultValue) as T
    }

    override suspend fun <T> getFlow(
        keys: List<String>,
        serializer: KSerializer<T>,
    ): Flow<T> = keys.toKey().let { key -> stateFlow.filter { it.key == key }.map { it.value as T } }

    override suspend fun remove(keys: List<String>): Unit =
        keys.toKey().let {
            dao.deleteLike("$it$KEY_DELIMITER%")
        }

    override suspend fun clear(): Unit = dao.deleteAll()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Int  = dao.count()

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$KEY_DELIMITER$v}" }

    public companion object {

        public const val KEY_DELIMITER: Char = '.'
    }
}
