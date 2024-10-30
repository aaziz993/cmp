package ai.tech.core.data.http

import ai.tech.core.data.http.model.client.HttpClientTrustStore
import io.ktor.client.*

public expect fun createHttpClient(
    trustStore: HttpClientTrustStore,
    block: HttpClientConfig<*>.() -> Unit = {}
): HttpClient