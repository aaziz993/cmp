@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:OptIn(InternalAPI::class)

package ai.tech.core.misc.network.http.server

import ai.tech.core.misc.network.http.client.JsonStream
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.PluginBuilder
import io.ktor.server.application.PluginInstance
import io.ktor.server.application.plugin
import io.ktor.server.application.pluginRegistry
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.RoutingCall
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlin.collections.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

public fun RoutingCall.receiveAsInputStream() = flow {
    val channel = receiveChannel()

    while (!channel.isClosedForRead) {
        emit(channel.readUTF8Line())
    }
}

public suspend fun RoutingCall.respondOutputStream(
    contentType: ContentType? = null,
    status: HttpStatusCode? = null,
    contentLength: Long? = null,
    flow: Flow<String>
) = respondBytesWriter(contentType, status, contentLength) {
    flow.collect { value ->
        writeStringUtf8("$value\n")
        flush()
    }
}

public suspend inline fun <reified T> RoutingCall.respondOutputStream(
    contentType: ContentType? = null,
    typeInfo: TypeInfo,

    status: HttpStatusCode? = null,
    contentLength: Long? = null,
    flow: Flow<T>
) {
    val suitableConverter = (application.plugin(ContentNegotiation).builder as PluginBuilder<ContentNegotiationConfig>).pluginConfig.registrations
        .filter { it.contentType == contentType }
        .map { it.converter }
        .firstOrNull()


    respondOutputStream(
        contentType ?: ContentType.Application.JsonStream, status, contentLength,
        flow.map { suitableConverter.serialize(contentType, charset, typeInfo, it) },
    )
}
