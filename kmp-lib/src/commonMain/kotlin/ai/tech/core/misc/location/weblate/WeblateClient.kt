package ai.tech.core.misc.location.weblate

import ai.tech.core.data.http.server.model.exception.HttpResponseException
import ai.tech.core.misc.location.weblate.model.WeblateConfig
import ai.tech.core.misc.location.weblate.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.weblate.model.WeblateUnitsResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
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
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    public suspend inline fun <reified T> request(
        path: String,
    ): Result<T> =
        httpClient.get("${config.address}$path") {
            header(HttpHeaders.Authorization, "Token  ${config.apiKey}")
        }.let {
            if (it.status == HttpStatusCode.OK) {
                Result.success(it.body<T>())
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun getTranslations(page: Int? = null): Result<WeblateTranslationsResponse> =
        request("/api/translations/?format=json${page?.let { "&page=$it" } ?: ""}")

    public suspend fun getUnits(page: Int? = null): Result<WeblateUnitsResponse> =
        request("/api/units/?format=json${page?.let { "&page=$it" } ?: ""}")
}
