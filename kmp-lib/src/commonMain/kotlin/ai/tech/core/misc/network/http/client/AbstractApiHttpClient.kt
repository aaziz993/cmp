package ai.tech.core.misc.network.http.client

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

public abstract class AbstractApiHttpClient(
    httpClient: HttpClient,
    address: String
) {

    protected val httpClient: HttpClient = httpClient.config {
        defaultRequest {
            url(address)
        }

        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                },
            )
        }

        configureHttpClient()
    }

    protected val ktorfit: Ktorfit = Ktorfit.Builder()
        .httpClient(httpClient).baseUrl(address).configureKtorfit().build()

    protected open fun HttpClientConfig<*>.configureHttpClient() = Unit

    protected open fun Ktorfit.Builder.configureKtorfit(): Ktorfit.Builder = this
}
