package ai.tech.core.misc.network.http.client

import ai.tech.core.misc.network.http.client.model.Pin
import io.ktor.client.*
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.readAllParts
import io.ktor.http.encodedPath
import io.ktor.http.path

private val httpPR: Regex = "^https?://.*".toRegex(RegexOption.IGNORE_CASE)

public val String.isHttpUrl: Boolean
    get() = matches(httpPR)

public val String.isValidHttpUrl: Boolean
    get() = try {
        Url(this).let { it.protocol == URLProtocol.HTTP || it.protocol == URLProtocol.HTTPS }
    }
    catch (e: Exception) {
        false
    }

public val String.encodedHttpUrl: String
    get() =
        URLBuilder()
            .apply {
                encodedPath = this@encodedHttpUrl
            }.buildString()

public val String.decodedHttpUrl: String
    get() =
        URLBuilder()
            .apply {
                path(this@decodedHttpUrl)
            }.buildString()

public expect fun createHttpClient(
    pins: List<Pin> = emptyList(),
    block: HttpClientConfig<*>.() -> Unit = {}
): HttpClient

public suspend fun MultiPartData.readParts(): List<PartData> {
    val parts = mutableListOf<PartData>()

    forEachPart(parts::add)

    return parts
}

public suspend fun MultiPartData.readFormData(): Map<String?, String> = readParts().associate { it.name to (it as PartData.FormItem).value }

