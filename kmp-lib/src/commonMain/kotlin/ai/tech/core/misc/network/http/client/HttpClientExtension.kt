@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package ai.tech.core.misc.network.http.client

import ai.tech.core.misc.consul.client.plugin.ConsulDiscovery
import ai.tech.core.misc.consul.model.config.LoadBalancer
import ai.tech.core.misc.network.http.asInputStream
import ai.tech.core.misc.network.http.client.model.Pin
import ai.tech.core.misc.type.multiple.filterValuesIsNotNull
import ai.tech.core.misc.type.serializablePropertyValues
import ai.tech.core.misc.type.serialization.decodeAnyFromString
import ai.tech.core.misc.type.serialization.encodeAnyToString
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

@Suppress("UNCHECKED_CAST")
public fun <T> HttpResponse.bodyAsInputStream(deserializer: kotlinx.serialization.DeserializationStrategy<T>): Flow<T> = flow() {

//    val json: ClientPluginInstance<ContentNegotiationConfig> = call.client.plugin(ContentNegotiation)
//
//    val r = json.config.registrations.filter { it.contentType.match(ContentType.Application.Json) }

    val channel = bodyAsChannel()

    channel.asInputStream { value ->
        emit(value?.let { Json.Default.decodeFromString(deserializer, it) } as T)
    }
}

public fun HttpResponse.bodyAsInputStream(): Flow<Any?> = flow() {

    val channel = bodyAsChannel()

    channel.asInputStream { value ->
        emit(value?.let { Json.Default.decodeAnyFromString(it) })
    }
}

