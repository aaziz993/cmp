@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:OptIn(InternalAPI::class)

package ai.tech.core.misc.network.http.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.oauth.OAuth1aExplicitProvider
import ai.tech.core.misc.consul.client.plugin.ConsulDiscovery
import ai.tech.core.misc.consul.model.config.LoadBalancer
import ai.tech.core.misc.network.http.client.model.Pin
import ai.tech.core.misc.type.multiple.asyncIterator
import ai.tech.core.misc.type.multiple.filterValuesIsNotNull
import ai.tech.core.misc.type.multiple.forEach
import ai.tech.core.misc.type.multiple.toByteWriteChannel
import ai.tech.core.misc.type.serializablePropertyValues
import ai.tech.core.misc.type.serialization.decodeAnyFromString
import ai.tech.core.misc.type.serialization.encodeAnyToString
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.AuthConfig
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

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

public fun HttpClient.converters(contentType: ContentType): List<ContentConverter>? =
    plugin(ContentNegotiation)
        .config
        .registrations
        .filter { it.contentTypeMatcher.contains(contentType) }
        .map { it.converter }

public fun HttpRequestBuilder.setBody(
    flow: Flow<String>,
    contentType: ContentType? = null,
    status: HttpStatusCode? = null,
    contentLength: Long? = null,
) = ChannelWriterContent(
    flow::toByteWriteChannel,
    contentType,
    status,
    contentLength,
)

public fun <T : Any> HttpRequestBuilder.setBody(
    flow: Flow<T>,
    contentType: ContentType? = null,
    status: HttpStatusCode? = null,
    contentLength: Long? = null
) {
    val contentType = headers[HttpHeaders.ContentType]?.let(ContentType::parse)
    val client: HttpClient

    val suitableConverter = client.converters(contentType!!.withoutParameters())
        ?.firstOrNull()

    suitableConverter.serialize()
}

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun <T : Any> HttpResponse.bodyAsInputStream(kClass: KClass<T>, charset: Charset = Charsets.UTF_8): Flow<T> = flow {
    val contentType = headers[HttpHeaders.ContentType]?.let(ContentType::parse)

    val suitableConverters = call.client.converters(contentType!!.withoutParameters())

    val channel = bodyAsChannel()

    if (suitableConverters == null) {
        channel.asyncIterator().forEach { value ->
            value?.let { emit(Json.Default.decodeFromString(kClass.serializer(), it)) }
        }
    }

    val typeInfo = TypeInfo(kClass)

    channel.asyncIterator().forEach { value ->
        value?.let { emit(suitableConverters!!.deserialize(ByteReadChannel(it, charset), typeInfo, charset) as T) }
    }
}

public inline fun <reified T : Any> HttpResponse.bodyAsInputStream(charset: Charset = Charsets.UTF_8): Flow<T> =
    bodyAsInputStream(T::class, charset)

public fun HttpResponse.bodyAsInputStream(): Flow<Any?> = flow {

    val channel = bodyAsChannel()

    channel.asyncIterator().forEach { value ->
        value?.let { emit(Json.Default.decodeAnyFromString(it)) }
    }
}
