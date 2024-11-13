package ai.tech.core.misc.location.localization.weblate

import ai.tech.core.misc.location.localization.weblate.model.WeblateConfig
import ai.tech.core.misc.location.localization.weblate.model.WeblateResponse
import ai.tech.core.misc.location.localization.weblate.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.localization.weblate.model.WeblateUnitsResponse
import ai.tech.core.misc.type.multiple.model.AbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class WeblateClient(
    httpClient: HttpClient,
    public val config: WeblateConfig,
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val ktorfit = Ktorfit.Builder().httpClient(
        httpClient.config {
            defaultRequest {
                header(HttpHeaders.Authorization, "Token  ${config.apiKey}")
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
        },
    ).baseUrl(config.address).build()

    private val weblateApi = ktorfit.createWeblateApi()

    public val translations: AsyncIterator<WeblateTranslationsResponse>
        get() = WeblateResponseAsyncIterator { weblateApi.getTranslations("json", it) }

    public val units: AsyncIterator<WeblateUnitsResponse>
        get() = WeblateResponseAsyncIterator { weblateApi.getUnits("json", it) }
}

private class WeblateResponseAsyncIterator<T : WeblateResponse<*>>(
    private val getResponse: suspend (page: Int?) -> T,
) : AbstractAsyncIterator<T>() {

    private var page: Int? = 0

    override suspend fun computeNext() {
        if (page == null) {
            done()
            return
        }

        setNext(
            getResponse(page).also {
                page = it.nextPage
            },
        )
    }
}
