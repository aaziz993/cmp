@file:OptIn(InternalAPI::class)

package ai.tech.core.data.crud.client.http

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import ai.tech.core.misc.auth.client.AuthService
import ai.tech.core.misc.network.http.client.AbstractApiHttpClient
import ai.tech.core.misc.type.serializer.decodeAnyFromString
import ai.tech.core.misc.type.serializer.json
import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.serializer

public open class CRUDClient<T : Any, ID : Any>(
    public val serializer: KSerializer<T>,
    httpClient: HttpClient,
    public val address: String,
    public val authService: AuthService? = null,
) : AbstractApiHttpClient(httpClient, address), CRUDRepository<T, ID> {

    private val api = ktorfit.createCRUDApi()

    private val jsonHeader = Headers.build {
        append(HttpHeaders.ContentType, ContentType.Application.Json)
    }

    override suspend fun <R> transactional(block: suspend CRUDRepository<T, ID>.() -> R): R {
        throw UnsupportedOperationException("Not supported by remote client yet")
    }

    override suspend fun insert(entities: List<T>): Unit = api.insert(entities)

    @Suppress("UNCHECKED_CAST")
    override suspend fun insertAndReturn(entities: List<T>): List<ID> = api.insertAndReturn(entities).execute {
        Json.Default.decodeFromString(ListSerializer(PolymorphicSerializer(Any::class)), it.bodyAsText()) as List<ID>
    }

    override suspend fun update(entities: List<T>): List<Boolean> = api.update(entities)

    override suspend fun update(entities: List<Map<String, Any?>>, predicate: BooleanVariable?): List<Long> =
        api.update(
            MultiPartFormDataContent(
                formData {
                    append("entities", Json.Default.encodeToString(entities), jsonHeader)
                    predicate?.let { append("predicate", it, jsonHeader) }
                },
            ),
        )

    override suspend fun upsert(entities: List<T>): Unit = api.upsert(entities)

    override fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<T> =
        flow {
            val channel = findHelper(null, sort, predicate, limitOffset).bodyAsChannel()

            while (!channel.isClosedForRead) {
                channel.readUTF8Line()?.let {
                    emit(Json.Default.decodeFromString(serializer, it))
                }
            }
        }

    @OptIn(InternalSerializationApi::class)
    override fun find(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?,
    ): Flow<List<Any?>> = flow {
        val channel = findHelper(projections, sort, predicate, limitOffset).bodyAsChannel()

        while (!channel.isClosedForRead) {
            channel.readUTF8Line()?.let {
                emit(Json.Default.decodeAnyFromString(JsonArray::class.serializer(), it) as List<Any?>)
            }
        }
    }

    override suspend fun delete(predicate: BooleanVariable?): Long =
        api.delete(predicate)

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T =
        api.aggregate(
            MultiPartFormDataContent(
                formData {
                    append("aggregate", aggregate, jsonHeader)
                    predicate?.let { append("predicate", it, jsonHeader) }
                },
            ),
        ).execute().takeIf { it.status != HttpStatusCode.NoContent }?.let { json.decodeFromString(PolymorphicSerializer(Any::class), it.bodyAsText()) } as T

    private suspend fun findHelper(
        projections: List<Variable>?,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?
    ): HttpResponse = api.find(
        MultiPartFormDataContent(
            formData {
                projections?.let { append("projections", it, jsonHeader) }
                sort?.let { append("sort", it, jsonHeader) }
                predicate?.let { append("predicate", it, jsonHeader) }
                limitOffset?.let { append("limitOffset", it, jsonHeader) }
            },
        ),
    ).execute()
}
