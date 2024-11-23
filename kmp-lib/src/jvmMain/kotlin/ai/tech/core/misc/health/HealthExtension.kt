package ai.tech.core.misc.health


import ai.tech.core.data.database.model.config.DBConnectionConfig
import ai.tech.core.misc.r2dbc.createR2dbcConnectionFactory
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.r2dbc.spi.Connection
import kotlinx.coroutines.reactive.awaitSingle

private suspend fun RoutingCall.respondHealth(isHealthy: Boolean) {
    if (isHealthy) {
        respond(HttpStatusCode.OK, mapOf("status" to "UP"))
    }
    else {
        respond(HttpStatusCode.ServiceUnavailable, mapOf("status" to "DOWN"))
    }
}

public fun Route.registerHttpHealthCheck(httpClient: HttpClient, path: String, url: String) {
    get(path) {
        val isHealthy = try {
            httpClient.get(url).status == HttpStatusCode.OK
        }
        catch (_: Exception) {
            false
        }

        call.respond(isHealthy)
    }
}

public fun Route.registerDBHealthCheck(path: String, config: DBConnectionConfig) {
    val connectionFactory = createR2dbcConnectionFactory(config)

    get(path) {

        var connection: Connection? = null

        val isHealthy = try {
            connection = connectionFactory.create().awaitSingle()

            connection?.createStatement("SELECT 1")?.execute()?.awaitSingle()

            true
        }
        catch (_: Exception) {
            false
        }
        finally {
            connection?.close()?.awaitSingle()
        }

        call.respondHealth(isHealthy)
    }
}
