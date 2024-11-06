package ai.tech.core.misc.location.weblate

import ai.tech.core.misc.network.http.server.model.exception.HttpResponseException
import ai.tech.core.misc.location.weblate.model.WeblateConfig
import ai.tech.core.misc.location.weblate.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.weblate.model.WeblateUnitsResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class WeblateClient(
    httpClient: HttpClient,
    public val config: WeblateConfig,
) {

    @OptIn(ExperimentalSerializationApi::class)
    public val httpClient: HttpClient = httpClient.config {
        defaultRequest {
            url(config.address)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Token  ${config.apiKey}")
        }

        install(ContentNegotiation) {

            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                },
            )
        }
    }

    @Throws(HttpResponseException::class)
    public suspend inline fun <reified T> request(
        path: String,
    ): T = httpClient.get(path).body<T>()

    public suspend fun getTranslations(page: Int? = null): WeblateTranslationsResponse = request("${PATH}translations/?format=json${page?.let { "&page=$it" } ?: ""}")

    public suspend fun getUnits(page: Int? = null): WeblateUnitsResponse = request("${PATH}units/?format=json${page?.let { "&page=$it" } ?: ""}")

    public companion object {

        public const val PATH: String = "/api/"
    }
}
