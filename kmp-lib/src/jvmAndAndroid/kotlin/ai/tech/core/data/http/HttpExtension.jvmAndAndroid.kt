package core.data.http

import ai.tech.core.data.http.model.client.HttpClientTrustStoreImpl
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

public actual fun createHttpClient(
    trustStore: HttpClientTrustStore,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient = HttpClient(OkHttp) {
    engine {
        config {
            (trustStore as HttpClientTrustStoreImpl?)?.let {
                sslSocketFactory(it.sslContext!!.socketFactory, it.trustManager)
            }
        }
    }
    block()
}
