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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

public open class CRUDClient<T : Any>(
    public val serializer: KSerializer<T>,
    httpClient: HttpClient,
    public val path: String,
    public val config: CRUDRepositoryConfig? = null,
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
            config?.saveAuth?.let { authService!!.auth(this) }

            header(HttpHeaders.ContentType, ContentType.Application.Json)

            setBody(Json.Default.encodeToString(entities))
        }
    }

    override suspend fun update(entities: List<T>): List<Boolean> =
        httpClient.post("$path/updateSafe") {
            config?.updateAuth?.let { authService!!.auth(this) }

            header(HttpHeaders.ContentType, ContentType.Application.Json)

            setBody(Json.Default.encodeToString(entities))
        }.body()

    override suspend fun update(entities: List<Map<String, Any?>>, predicate: BooleanVariable?): List<Long> =
        httpClient.post("$path/update") {
            config?.updateAuth?.let { authService!!.auth(this) }

            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("entities", Json.Default.encodeToString(entities), jsonHeader)
                        predicate?.let { append("predicate", Json.Default.encodeToString(it), jsonHeader) }
                    },
                ),
            )
        }.body()

    override suspend fun find(sort: List<Order>?, predicate: BooleanVariable?): Flow<T> =
        findHelper(null, sort, predicate, null).bodyAsChannel().let {
            flow {
                while (!it.isClosedForRead) {
                    it.readUTF8Line()?.let {
                        emit(Json.Default.decodeFromString(serializer, it))
                    }
                }
            }
        }

    override suspend fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset): Page<T> =
        Json.Default.decodeFromString(findHelper(null, sort, predicate, limitOffset).bodyAsText())

    override suspend fun find(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
    ): Flow<List<Any?>> = findHelper(projections, sort, predicate, null).bodyAsChannel().let {
        flow {
            while (!it.isClosedForRead) {
                it.readUTF8Line()?.let {
                    emit(jsonCRUD.decode<List<*>>(it))
                }
            }
        }
    }

    override suspend fun find(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset
    ): Page<List<Any?>> =
        findHelper(projections, sort, predicate, limitOffset).bodyAsText()

    override suspend fun delete(predicate: BooleanVariable?): Long =
        httpClient.post("$path/delete") {
            config?.deleteAuth?.let { authService!!.auth(this) }

            header(HttpHeaders.ContentType, ContentType.Application.Json)

            predicate?.let { setBody(Json.Default.encodeToString(it)) }
        }.body()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T =
        jsonCRUD.decode<Any?>(
            httpClient.post("$path/aggregate") {
                config?.readAuth?.let { authService!!.auth(this) }

                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("aggregate", Json.Default.encodeToString(aggregate), jsonHeader)
                            predicate?.let { append("predicate", Json.Default.encodeToString(it), jsonHeader) }
                        },
                    ),
                )
            }.bodyAsText(),
        ) as T

    private suspend fun findHelper(
        projections: List<Variable>?,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?
    ): HttpResponse =
        httpClient.post("$path/find") {
            config?.readAuth?.let { authService!!.auth(this) }

            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("projections", Json.Default.encodeToString(projections), jsonHeader)
                        append("sort", Json.Default.encodeToString(sort), jsonHeader)
                        append("predicate", Json.Default.encodeToString(predicate), jsonHeader)
                        append("limitOffset", Json.Default.encodeToString(limitOffset), jsonHeader)
                    },
                ),
            )
        }
}
