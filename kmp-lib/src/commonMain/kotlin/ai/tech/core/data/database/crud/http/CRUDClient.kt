@file:OptIn(InternalAPI::class)

package ai.tech.core.data.database.crud.http

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.database.crud.model.LimitOffset
import ai.tech.core.data.database.crud.model.Order
import ai.tech.core.data.database.crud.model.Page
import ai.tech.core.data.database.model.config.CRUDRepositoryConfig
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import ai.tech.core.misc.auth.client.ClientAuthService
import ai.tech.core.misc.type.decodeAnyFromJsonElement
import ai.tech.core.misc.type.decodeAnyFromString
import ai.tech.core.misc.type.json
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.serializer

public open class CRUDClient<T : Any>(
    public val serializer: KSerializer<T>,
    httpClient: HttpClient,
    public val path: String,
    public val config: CRUDRepositoryConfig? = null,
    public val authProvider: String? = null,
    public val authService: ClientAuthService? = null,
) : CRUDRepository<T> {

    public val httpClient: HttpClient = httpClient.config {
        defaultRequest {
            url(path)
        }
    }

    private val jsonHeader = Headers.build {
        append(HttpHeaders.ContentType, ContentType.Application.Json)
    }

    override suspend fun <R> transactional(byUser: String?, block: suspend CRUDRepository<T>.() -> R): R {
        throw UnsupportedOperationException("Not supported by remote client")
    }

    override suspend fun insert(entities: List<T>) {
        httpClient.post("/insert") {
            if (config?.saveAuth != null && authProvider in config.saveAuth.providers) {
                authService!!.auth(this)
            }

            header(HttpHeaders.ContentType, ContentType.Application.Json)

            setBody(entities)
        }
    }

    override suspend fun update(entities: List<T>): List<Boolean> =
        httpClient.post("/updateTypeSafe") {
            if (config?.updateAuth != null && authProvider in config.updateAuth.providers) {
                authService!!.auth(this)
            }

            header(HttpHeaders.ContentType, ContentType.Application.Json)

            setBody(Json.Default.encodeToString(entities))
        }.body()

    override suspend fun update(entities: List<Map<String, Any?>>, predicate: BooleanVariable?): List<Long> =
        httpClient.post("/update") {
            if (config?.updateAuth != null && authProvider in config.updateAuth.providers) {
                authService!!.auth(this)
            }

            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("entities", Json.Default.encodeToString(entities), jsonHeader)
                        predicate?.let { append("predicate", it, jsonHeader) }
                    },
                ),
            )
        }.body()

    override fun find(sort: List<Order>?, predicate: BooleanVariable?): Flow<T> =
        flow {
            val channel = findHelper(null, sort, predicate, null).bodyAsChannel()

            while (!channel.isClosedForRead) {
                channel.readUTF8Line()?.let {
                    emit(Json.Default.decodeFromString(serializer, it))
                }
            }
        }

    override suspend fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset): Page<T> =
        Json.Default.decodeFromString(findHelper(null, sort, predicate, limitOffset).bodyAsText())

    @OptIn(InternalSerializationApi::class)
    override fun find(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
    ): Flow<List<Any?>> = flow {
        val channel = findHelper(projections, sort, predicate, null).bodyAsChannel()

        while (!channel.isClosedForRead) {
            channel.readUTF8Line()?.let {
                emit(Json.Default.decodeAnyFromString(JsonArray::class.serializer(), it) as List<Any?>)
            }
        }
    }

    override suspend fun find(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset
    ): Page<List<Any?>> =
        Json.Default.decodeFromString<JsonObject>(findHelper(projections, sort, predicate, limitOffset).bodyAsText()).let {
            Page(Json.Default.decodeAnyFromJsonElement(it["entities"] as JsonArray) as List<List<Any?>>, (it["totalCount"] as JsonPrimitive).long)
        }

    override suspend fun delete(predicate: BooleanVariable?): Long =
        httpClient.post("/delete") {
            if (config?.deleteAuth != null && authProvider in config.deleteAuth.providers) {
                authService!!.auth(this)
            }

            header(HttpHeaders.ContentType, ContentType.Application.Json)

            predicate?.let { setBody(Json.Default.encodeToString(it)) }
        }.body()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T =
        httpClient.post("/aggregate") {
            if (config?.readAuth != null && authProvider in config.readAuth.providers) {
                authService!!.auth(this)
            }

            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("aggregate", aggregate, jsonHeader)
                        predicate?.let { append("predicate", it, jsonHeader) }
                    },
                ),
            )
        }.takeIf { it.status != HttpStatusCode.NoContent }?.let {
            json.decodeFromString(PolymorphicSerializer(Any::class), it.bodyAsText())
        } as T

    private suspend fun findHelper(
        projections: List<Variable>?,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?
    ): HttpResponse =
        httpClient.post("/find") {
            if (config?.readAuth != null && authProvider in config.readAuth.providers) {
                authService!!.auth(this)
            }

            setBody(
                MultiPartFormDataContent(
                    formData {
                        projections?.let { append("projections", it, jsonHeader) }
                        sort?.let { append("sort", it, jsonHeader) }
                        predicate?.let { append("predicate", it, jsonHeader) }
                        limitOffset?.let { append("limitOffset", it, jsonHeader) }
                    },
                ),
            )
        }
}
