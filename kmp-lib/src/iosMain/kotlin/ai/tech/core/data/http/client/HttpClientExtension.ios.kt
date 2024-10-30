package ai.tech.core.data.http.client

import ai.tech.core.data.http.client.model.Pin
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.engine.darwin.certificates.*

public actual fun createHttpClient(
    pins: List<Pin>,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient = HttpClient(Darwin) {
    engine {
        val builder = pins.fold(CertificatePinner.Builder()) { acc, v ->
            acc.add(v.pattern, *v.pins.toTypedArray())
        }
        handleChallenge(builder.build())
    }
    block()
}
