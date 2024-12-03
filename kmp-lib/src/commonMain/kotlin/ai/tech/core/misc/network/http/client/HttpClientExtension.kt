@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:OptIn(InternalAPI::class)

package ai.tech.core.misc.network.http.client

import ai.tech.core.misc.consul.client.plugin.ConsulDiscovery
import ai.tech.core.misc.consul.model.config.LoadBalancer
import ai.tech.core.misc.network.http.client.model.Pin
import ai.tech.core.misc.network.http.inputStream
import ai.tech.core.misc.type.multiple.filterValuesIsNotNull
import ai.tech.core.misc.type.serializablePropertyValues
import ai.tech.core.misc.type.serialization.decodeAnyFromString
import ai.tech.core.misc.type.serialization.encodeAnyToString
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val httpPR: Regex = "^https?://.*".toRegex(RegexOption.IGNORE_CASE)

public val ContentType.Application.JsonStream: ContentType
    get() = ContentType.parse("application/stream+json")

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

public fun HttpClient.converters(responseContentType: ContentType): List<ContentConverter>? =
    plugin(ContentNegotiation)
        .config
        .registrations
        .filter { it.contentTypeMatcher.contains(responseContentType) }
        .map { it.converter }
        .takeIf { it.isNotEmpty() }

@Suppress("UNCHECKED_CAST")
public fun <T> HttpResponse.bodyAsInputStream(typeInfo: TypeInfo, charset: Charset = Charsets.UTF_8): Flow<T> = flow {
    val contentType = headers[HttpHeaders.ContentType]?.let(ContentType::parse)

    val suitableConverters = call.client.converters(contentType!!.withoutParameters())

    val channel = bodyAsChannel()

    channel.inputStream.map { value ->
        emit(value?.let { suitableConverters!!.deserialize(ByteReadChannel(it), typeInfo, charset) } as T)
    }
}

public inline fun <reified T> HttpResponse.bodyAsInputStream(charset: Charset = Charsets.UTF_8): Flow<T> =
    bodyAsInputStream(typeInfo<T>(), charset)

public fun HttpResponse.bodyAsInputStream(): Flow<Any?> = flow {

    val channel = bodyAsChannel()

    channel.inputStream.map { value ->
        emit(value?.let { Json.Default.decodeAnyFromString(it) })
    }
}

