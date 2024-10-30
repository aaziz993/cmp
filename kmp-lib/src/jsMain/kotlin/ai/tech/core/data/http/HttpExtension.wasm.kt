package ai.tech.core.data.http

import ai.tech.core.data.http.model.client.HttpClientTrustStore
import io.ktor.client.*
import io.ktor.client.engine.js.*

public actual fun createHttpClient(
    trustStore: HttpClientTrustStore,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient =
    HttpClient(Js) {
        block()
    }


