package ai.tech.core.data.http.server.model.exception

import io.ktor.http.*

public class HttpResponseException(public val status: HttpStatusCode, public val responseBody: String) :
    Throwable() {
    override val message: String
        get() = "{\"code\": ${status.value}, \"error_response\": $responseBody, \"message\": ${cause?.message}}"
}
