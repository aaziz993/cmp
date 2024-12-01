package ai.tech.core.data.crud.server.http

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.http.model.HttpOperation
import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.plugin.auth.authOpt
import ai.tech.core.misc.type.serialization.decodeAnyFromJsonElement
import ai.tech.core.misc.type.serialization.encodeAnyToString
import ai.tech.core.misc.type.serialization.json
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(InternalSerializationApi::class)
@Suppress("FunctionName", "UNCHECKED_CAST")
public inline fun <reified T : Any> Routing.CrudRouting(
    path: String,
    repository: CRUDRepository<T>,
    readAuth: AuthResource? = null,
    writeAuth: AuthResource? = readAuth,
) {
    route(path) {
        authOpt(writeAuth) {
            post("transaction") {

                val channel = call.receiveChannel()

                while (!channel.isClosedForRead) {
                    channel.readUTF8Line()?.let {
                        val operation: HttpOperation = Json.Default.decodeFromString(it)
                        when (operation) {
                            is HttpOperation.Insert<*> -> repository.insert(operation.values as List<T>)
                            is HttpOperation.InsertAndReturn<*> -> repository.insertAndReturn(operation.values as List<T>)
                            is HttpOperation.Update<*> -> repository.update(operation.values as List<T>)
                            is HttpOperation.UpdateUntyped -> repository.update(
                                Json.Default.decodeAnyFromJsonElement(operation.propertyValues) as List<Map<String, Any?>>,
                                operation.predicate,
                            )

                            is HttpOperation.Upsert<*> -> repository.upsert(operation.values as List<T>)
                            else -> Unit
                        }

                    }
                }
            }

            put("insert") {
                repository.insert(call.receive<HttpOperation.Insert<T>>().values)
                call.respond(HttpStatusCode.OK, "Successful")
            }

            put("insertAndReturn") {
                call.respond(HttpStatusCode.OK, repository.insertAndReturn(call.receive<HttpOperation.InsertAndReturn<T>>().values))
            }

            post("update") {
                call.respond(HttpStatusCode.OK, repository.update(call.receive<HttpOperation.Update<T>>().values))
            }

            post("updateUntyped") {
                with(call.receive<HttpOperation.UpdateUntyped>()) {
                    call.respond(
                        HttpStatusCode.OK,
                        repository.update(
                            Json.Default.decodeAnyFromJsonElement(propertyValues) as List<Map<String, Any?>>,
                            predicate,
                        ),
                    )
                }
            }

            put("upsert") {
                call.respond(HttpStatusCode.OK, repository.upsert(call.receive<HttpOperation.Upsert<T>>().values))
            }

            post("delete") {
                call.respond(HttpStatusCode.OK, repository.delete(call.receive<HttpOperation.Delete>().predicate))
            }
        }

        authOpt(readAuth) {
            post("find") {
                with(call.receive<HttpOperation.Find>()) {
                    if (projections == null) {
                        repository.find(sort, predicate, limitOffset).let {
                            call.respondBytesWriter(ContentType.parse("application/stream+json"), HttpStatusCode.OK) {
                                it.collect {
                                    writeStringUtf8("${Json.Default.encodeToString(it)}\n")
                                    flush()
                                }
                            }
                        }
                    }
                    else {
                        repository.find(projections, sort, predicate, limitOffset).let {
                            call.respondBytesWriter(ContentType.parse("application/stream+json"), HttpStatusCode.OK) {
                                it.collect {
                                    writeStringUtf8("${Json.Default.encodeAnyToString(it)}\n")
                                    flush()
                                }
                            }
                        }
                    }
                }
            }

            post("aggregate") {
                with(call.receive<HttpOperation.Aggregate>()) {
                    repository.aggregate(aggregate, predicate)?.let {
                        call.respond(HttpStatusCode.OK, json.encodeToString(PolymorphicSerializer(Any::class), it))
                    } ?: call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}

