package ai.tech.core.misc.network.http.client

import ai.tech.core.misc.network.http.client.model.Pin
import io.ktor.client.*
import io.ktor.client.engine.js.*

public actual fun createHttpClient(
    pins: List<Pin>,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient = HttpClient(Js, block)


