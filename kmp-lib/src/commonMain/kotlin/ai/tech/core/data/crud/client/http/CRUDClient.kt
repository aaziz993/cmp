@file:OptIn(InternalAPI::class)

package ai.tech.core.data.crud.client.http

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.http.model.HttpOperation
import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import ai.tech.core.data.transaction.Transaction
import ai.tech.core.misc.auth.client.AbstractAuthService
import ai.tech.core.misc.network.http.client.AbstractApiHttpClient
import ai.tech.core.misc.network.http.client.bodyAsInputStream
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

public class BodyOutputStream : OutgoingContent.WriteChannelContent() {

    private lateinit var channel: ByteWriteChannel

    override suspend fun writeTo(channel: ByteWriteChannel) {
        this.channel = channel
    }

    public suspend fun write(value: String) {
        channel.writeStringUtf8("$value\n")
    }

    public suspend fun writeOutputStream(flow: Flow<String>): Unit = flow.collect { value ->
        write(value)
    }
}

public open class CRUDClient<T : Any>(
    public val serializer: KSerializer<T>,
    httpClient: HttpClient,
    public val address: String,
    public val authService: AbstractAuthService? = null,
) : AbstractApiHttpClient(httpClient, address), CRUDRepository<T> {

    protected val lock: ReentrantLock = reentrantLock()

    override val json: Json = Json.Default

    private val api: CRUDApi = ktorfit.createCRUDApi()

    private var transactionChannel: ByteWriteChannel? = null

    @Suppress("UNCHECKED_CAST")
    override suspend fun <R> transactional(block: suspend CRUDRepository<T>.(Transaction) -> R): R = lock.withLock {
        try {
            var result: R? = null

            api.transaction {
                body = object : OutgoingContent.WriteChannelContent() {
                    override suspend fun writeTo(channel: ByteWriteChannel) {
                        transactionChannel = channel
                    }
                }

                result = block(
                    object : Transaction {
                        override suspend fun rollback() = throw UnsupportedOperationException()

                        override suspend fun commit() = throw UnsupportedOperationException()
                    },
                )
            }

            result as R
        }
        catch (e: Exception) {
            throw e
        }
        finally {
            transactionChannel?.flushAndClose()
            transactionChannel = null
        }
    }

    override suspend fun insert(entities: List<T>): Unit = lock.withLock {
        val operation = HttpOperation.Insert(entities)
        if (transactionChannel == null) {
            return api.insert(operation)
        }
        transaction(operation)
    }

    override suspend fun insertAndReturn(entities: List<T>): List<T> = lock.withLock {
        val operation = HttpOperation.InsertAndReturn(entities)
        require(transactionChannel == null) {
            "In remote transaction \"insertAndReturn\" is not supported"
            return api.insertAndReturn(operation).execute(HttpResponse::body)
        }
        transaction(operation)
        return emptyList()
    }

    override suspend fun update(entities: List<T>): List<Boolean> = lock.withLock {
        val operation = HttpOperation.Update(entities)
        if (transactionChannel == null) {
            return api.update(operation)
        }
        transaction(operation)
        return emptyList()
    }

    override suspend fun update(propertyValues: List<Map<String, Any?>>, predicate: BooleanVariable?): Long = lock.withLock {
        val operation = HttpOperation.UpdateUntyped(propertyValues, predicate)
        if (transactionChannel == null) {
            return api.update(operation)
        }
        transaction(operation)
        return 0
    }

    override suspend fun upsert(entities: List<T>): List<T> = lock.withLock {
        val operation = HttpOperation.Upsert(entities)
        if (transactionChannel == null) {
            return api.upsert(operation).execute(HttpResponse::body)
        }
        transaction(operation)
        return emptyList()
    }

    override fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<T> = lock.withLock {
        require(transactionChannel == null) {
            "In remote transaction \"find\" is not supported"
        }
        return flow {
            emitAll(api.find(HttpOperation.Find(null, sort, predicate, limitOffset)).execute().bodyAsInputStream<T>(serializer))
        }
    }

    @Suppress("UNCHECKED_CAST")
    @OptIn(InternalSerializationApi::class)
    override fun find(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?,
    ): Flow<List<Any?>> = lock.withLock {
        require(transactionChannel == null) {
            "In remote transaction \"find\" is not supported"
        }
        return flow {
            emitAll(api.find(HttpOperation.Find(projections, sort, predicate, limitOffset)).execute().bodyAsInputStream() as Flow<List<Any?>>)
        }
    }

    override suspend fun delete(predicate: BooleanVariable?): Long = lock.withLock {
        val operation = HttpOperation.Delete(predicate)
        if (transactionChannel == null) {
            return api.delete(operation)
        }
        transaction(operation)
        return 0
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T = lock.withLock {
        require(transactionChannel == null) {
            "In remote transaction \"aggregate\" is not supported"
        }
        return api.aggregate(
            HttpOperation.Aggregate(aggregate, predicate),
        ).execute().takeIf { it.status != HttpStatusCode.NoContent }?.let { json.decodeFromString(PolymorphicSerializer(Any::class), it.bodyAsText()) } as T
    }

    private suspend fun transaction(operation: HttpOperation) {
        transactionChannel!!.writeStringUtf8("${Json.Default.encodeToString(operation)}\n")
        transactionChannel!!.flush()
    }
}
