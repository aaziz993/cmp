package ai.tech.core.data.http

import ai.tech.core.data.http.model.IosHttpClientTrustStore
import ai.tech.core.data.http.model.client.HttpClientTrustStore
import io.ktor.client.*
import io.ktor.client.engine.darwin.*

public actual fun createHttpClient(
    trustStore: HttpClientTrustStore,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient = HttpClient(Darwin) {
    engine {
        (trustStore as IosHttpClientTrustStore?)?.let { handleChallenge(it) }
    }
    block()
}
