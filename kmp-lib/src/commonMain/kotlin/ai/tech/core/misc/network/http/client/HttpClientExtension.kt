package ai.tech.core.misc.network.http.client

import ai.tech.core.misc.consul.client.plugin.ConsulDiscovery
import ai.tech.core.misc.consul.model.config.LoadBalancer
import ai.tech.core.misc.network.http.client.model.Pin
import ai.tech.core.misc.type.multiple.filterValuesIsNotNull
import ai.tech.core.misc.type.serializablePropertyValues
import ai.tech.core.misc.type.serializer.encodeAnyToString
import io.ktor.client.*
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.encodedPath
import io.ktor.http.path
import kotlinx.serialization.json.Json

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

public val String.httpUrl: Url
    get() = Url(this)

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

public val Any.serializableHttpQueryParameters
    get() = serializablePropertyValues.filterValuesIsNotNull().mapValues { Json.Default.encodeAnyToString(it.value) }

public expect fun createHttpClient(
    pins: List<Pin> = emptyList(),
    block: HttpClientConfig<*>.() -> Unit = {}
): HttpClient

public fun HttpClientConfig<*>.consulDiscovery(
    address: String,
    loadBalancer: LoadBalancer = LoadBalancer.ROUND_ROBIN,
    serviceName: String
) = install(ConsulDiscovery(address, loadBalancer, serviceName))

public suspend fun MultiPartData.readParts(): List<PartData> = mutableListOf<PartData>().apply {
    forEachPart(::add)
}

public suspend fun MultiPartData.readFormData(): Map<String?, String> =
    readParts().associate { (it as PartData.FormItem).let { it.name to it.value } }

