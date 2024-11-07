package ai.tech.core.misc.location.localization.weblate

import ai.tech.core.misc.location.localization.weblate.model.WeblateConfig
import ai.tech.core.misc.location.localization.weblate.model.WeblateResponse
import ai.tech.core.misc.location.localization.weblate.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.localization.weblate.model.WeblateUnitsResponse
import ai.tech.core.misc.type.multiple.model.AbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlin.reflect.typeOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class WeblateClient(
    httpClient: HttpClient,
    public val config: WeblateConfig,
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient: HttpClient = httpClient.config {
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

    public val translations: AsyncIterator<WeblateTranslationsResponse>
        get() = WeblateResponseAsyncIterator(httpClient, "${PATH}translations/?format=json", typeInfo<WeblateTranslationsResponse>())

    public val units: AsyncIterator<WeblateUnitsResponse>
        get() = WeblateResponseAsyncIterator(httpClient, "${PATH}units/?format=json", typeInfo<WeblateUnitsResponse>())

    public companion object {

        public const val PATH: String = "/api/"
    }
}

private class WeblateResponseAsyncIterator<T : WeblateResponse<*>>(private val httpClient: HttpClient, path: String, private val typeInfo: TypeInfo) : AbstractAsyncIterator<T>() {

    private var path: String? = path

    override suspend fun computeNext() {
        if (path == null) {
            done()
            return
        }

        setNext(
            httpClient.get(path!!).body<T>(typeInfo).also {
                path = it.next
            },
        )
    }
}
