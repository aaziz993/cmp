package ai.tech.core.misc.location.localization.weblate.client

import ai.tech.core.misc.location.localization.weblate.client.model.WeblateResponse
import ai.tech.core.misc.location.localization.weblate.client.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.localization.weblate.client.model.WeblateUnitsResponse
import ai.tech.core.misc.network.http.client.configApi
import ai.tech.core.misc.type.multiple.model.AbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi

public class WeblateClient(
    httpClient: HttpClient,
    public val address: String,
    public val apiKey: String,
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val ktorfit = Ktorfit.Builder().httpClient(
        httpClient.configApi {
            defaultRequest {
                header(HttpHeaders.Authorization, "Token  $apiKey")
            }
        },
    ).baseUrl(address).build()

    private val api = ktorfit.createWeblateApi()

    public val translations: AsyncIterator<WeblateTranslationsResponse>
        get() = WeblateResponseAsyncIterator { api.getTranslations("json", it) }

    public val units: AsyncIterator<WeblateUnitsResponse>
        get() = WeblateResponseAsyncIterator { api.getUnits("json", it) }
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
