package ai.tech.core.misc.network.http.client

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

public abstract class AbstractApiHttpClient(
    httpClient: HttpClient,
    address: String
) {

    protected open val json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    protected val httpClient: HttpClient = httpClient.config {
        defaultRequest {
            configureDefaultRequest()
            url(address)
        }

        install(ContentNegotiation) {
            json(json)
        }
    }

    protected val ktorfit: Ktorfit = Ktorfit.Builder()
        .httpClient(httpClient).baseUrl(address).configureKtorfit().build()

    protected open fun DefaultRequest.DefaultRequestBuilder.configureDefaultRequest() = Unit

    protected open fun Ktorfit.Builder.configureKtorfit(): Ktorfit.Builder = this
}
