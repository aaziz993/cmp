package ai.tech.core.misc.network.http.client.model.exception

import io.ktor.http.HttpStatusCode

public class HttpResponseException(public val status: HttpStatusCode, public val responseBody: String) :
    Throwable("{\"statusCode\": ${status.value}, \"responseBody\": $responseBody")

public fun errorHttpStatus(statusCode: HttpStatusCode, responseBody: String): Nothing = throw HttpResponseException(statusCode, responseBody)
