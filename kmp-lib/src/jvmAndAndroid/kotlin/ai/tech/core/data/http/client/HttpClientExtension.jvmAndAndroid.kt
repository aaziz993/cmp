package ai.tech.core.data.http.client

import ai.tech.core.data.http.client.model.Pin
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
