package ai.tech.core.misc.network.http.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingContext

public suspend fun RoutingContext.handleHttpRequest(body: suspend () -> Unit) {
    try {
        body()
    }
    catch (e: Throwable) {
        call.respondText(e.stackTraceToString(), status = HttpStatusCode.InternalServerError)
        e.printStackTrace()
    }
}
