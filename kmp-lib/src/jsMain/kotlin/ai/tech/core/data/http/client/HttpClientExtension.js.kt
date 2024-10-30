package ai.tech.core.data.http.client

import ai.tech.core.data.http.client.model.Pin
import ai.tech.core.data.http.model.client.HttpClientTrustStore
import io.ktor.client.*
import io.ktor.client.engine.js.*

public actual fun createHttpClient(
    pins: List<Pin>,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient = HttpClient(Js, block)


