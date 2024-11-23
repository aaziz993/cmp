package ai.tech.core.misc.location.localization.weblate.client

import ai.tech.core.misc.location.localization.weblate.client.model.WeblateResponse
import ai.tech.core.misc.location.localization.weblate.client.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.localization.weblate.client.model.WeblateUnitsResponse
import ai.tech.core.misc.network.http.client.AbstractApiHttpClient
import ai.tech.core.misc.type.multiple.model.AbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

public class WeblateClient(
    httpClient: HttpClient,
    public val address: String,
    public val apiKey: String,
) : AbstractApiHttpClient(httpClient,address) {

    private val api = ktorfit.createWeblateApi()

    override fun HttpClientConfig<*>.configureHttpClient() {
        defaultRequest {
            header(HttpHeaders.Authorization, "Token  $apiKey")
        }
    }

    public suspend fun get(path: String, page: Int? = null): HttpResponse =
        api.get(path, "json", page).execute()

    public suspend fun getTranslations(page: Int? = null): WeblateTranslationsResponse =
        api.getTranslations("json", page)

    public suspend fun getUnits(page: Int? = null): WeblateUnitsResponse =
        api.getUnits("json", page)

    public val translations: AsyncIterator<WeblateTranslationsResponse>
        get() = WeblateResponseAsyncIterator("api/translations") { api.get(it).body() }

    public val units: AsyncIterator<WeblateUnitsResponse>
        get() = WeblateResponseAsyncIterator("api/units") { api.get(it).body() }
}

private class WeblateResponseAsyncIterator<T : WeblateResponse<*>>(
    private var path: String?,
    private val get: suspend (path: String) -> T,
) : AbstractAsyncIterator<T>() {

    override suspend fun computeNext() {
        if (path == null) {
            done()
            return
        }

        setNext(
            get(path!!).also {
                path = it.next
            },
        )
    }
}
