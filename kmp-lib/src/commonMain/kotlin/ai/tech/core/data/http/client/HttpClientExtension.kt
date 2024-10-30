package ai.tech.core.data.http.client

import ai.tech.core.data.http.client.model.Pin
import io.ktor.client.*

public expect fun createHttpClient(
    pins: List<Pin>,
    block: HttpClientConfig<*>.() -> Unit = {}
): HttpClient