package ai.tech.core.misc.network.http.client

import ai.tech.core.misc.network.http.client.model.Pin
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

public actual fun createHttpClient(
    pins: List<Pin>,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient = HttpClient(OkHttp) {
    engine {
        val certificatePinner = pins.fold(CertificatePinner.Builder()) { acc, v ->
            acc.add(v.pattern, *v.pins.toTypedArray())
        }.build()

        preconfigured = OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .build()
    }
    block()
}
