package ai.tech.core.data.crud.http.server

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.crud.model.config.CRUDRepositoryConfig
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.network.http.client.readFormData
import ai.tech.core.misc.plugin.auth.authOpt
import ai.tech.core.misc.type.serializer.decodeAnyFromString
import ai.tech.core.misc.type.serializer.encodeAnyToJsonElement
import ai.tech.core.misc.type.serializer.encodeAnyToString
import ai.tech.core.misc.type.serializer.json
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
@Suppress("FunctionName", "UNCHECKED_CAST")
public inline fun <reified T : Any> Routing.CrudRouting(
    path: String,
    repository: CRUDRepository<T>,
    readAuth: AuthResource? = null,
    saveAuth: AuthResource? = readAuth,
    updateAuth: AuthResource? = readAuth,
    deleteAuth: AuthResource? = readAuth,
) {
    authOpt(saveAuth) {
        put("$path/insert") {
            repository.insert(call.receive<List<T>>())
            call.respondText("Inserted", status = HttpStatusCode.OK)
        }
    }


    authOpt(updateAuth) {
        post("$path/updateTypeSafe") {
            call.respond(HttpStatusCode.OK, repository.update(call.receive<T>()))
        }

        post("$path/update") {
            val form = call.receiveMultipart().readFormData()

            call.respond(
                HttpStatusCode.OK,
                repository.update(
                    Json.Default.decodeAnyFromString(
                        JsonArray::class.serializer(),
                        form["entities"]!!,
                    ) as List<Map<String, Any?>>,
                    form["predicate"]?.let { Json.Default.decodeFromString(it) },
                ),
            )
        }
    }

    authOpt(readAuth) {
        post("$path/find") {
            val form = call.receiveMultipart().readFormData()

            var projections: List<Variable>? = form["projections"]?.let { Json.Default.decodeFromString(it) }

            var sort: List<Order>? = form["sort"]?.let { Json.Default.decodeFromString(it) }

            var predicate: BooleanVariable? = form["predicate"]?.let { Json.Default.decodeFromString(it) }

            var limitOffset: LimitOffset? = form["limitOffset"]?.let { Json.Default.decodeFromString(it) }


            if (projections == null) {
                if (limitOffset == null) {
                    repository.find(sort, predicate).let {
                        call.respondBytesWriter(ContentType.parse("application/stream+json"), HttpStatusCode.OK) {
                            it.collect {
                                writeStringUtf8("${Json.Default.encodeToString(it)}\n")
                                flush()
                            }
                        }
                    }
                }
                else {
                    call.respond(HttpStatusCode.OK, repository.find(sort, predicate, limitOffset))
                }
            }
            else {
                if (limitOffset == null) {
                    repository.find(projections, sort, predicate).let {
                        call.respondBytesWriter(ContentType.parse("application/stream+json"), HttpStatusCode.OK) {
                            it.collect {
                                writeStringUtf8("${Json.Default.encodeAnyToString(it)}\n")
                                flush()
                            }
                        }
                    }
                }
                else {
                    val page = repository.find(projections, sort, predicate, limitOffset)
                    call.respondText(
                        Json.Default.encodeToString(
                            JsonObject(
                                mapOf(
                                    "entities" to Json.Default.encodeAnyToJsonElement(page.entities),
                                    "totalCount" to JsonPrimitive(page.totalCount),
                                ),
                            ),
                        ),
                        status = HttpStatusCode.OK,
                    )
                }
            }
        }
    }

    authOpt(deleteAuth) {
        post("$path/delete") {
            call.respond(HttpStatusCode.OK, repository.delete(call.receiveNullable()))
        }
    }

    authOpt(readAuth) {
        post("$path/aggregate") {
            val form = call.receiveMultipart().readFormData()
            
            repository.aggregate(
                Json.Default.decodeFromString<AggregateExpression<Nothing>>(form["aggregate"]!!) as AggregateExpression<Any?>,
                form["predicate"]?.let { Json.Default.decodeFromString(it) },
            )?.let {
                call.respondText(json.encodeToString(PolymorphicSerializer(Any::class), it), status = HttpStatusCode.OK)
            } ?: call.respond(HttpStatusCode.NoContent)
        }
    }
}

