@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:OptIn(InternalAPI::class)

package ai.tech.core.misc.network.http.server

import ai.tech.core.misc.network.http.client.JsonStream
import ai.tech.core.misc.type.multiple.asyncIterator
import ai.tech.core.misc.type.multiple.toByteWriteChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConverter
import io.ktor.server.application.Application
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
import io.ktor.util.cio.ChannelWriteException
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeByteArray
import io.ktor.utils.io.writeString
import io.ktor.utils.io.writeStringUtf8
import kotlin.collections.map
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlinx.uuid.fromString

@Suppress("UNCHECKED_CAST")
public fun Application.converters(contentType: ContentType): List<ContentConverter>? =
    (plugin(ContentNegotiation).builder as PluginBuilder<ContentNegotiationConfig>)
        .pluginConfig
        .registrations
        .filter { it.contentType == contentType }
        .map { it.converter }
        .takeIf { it.isNotEmpty() }

public suspend fun RoutingCall.receiveInputStream() =
    receiveChannel().asyncIterator()

public suspend fun RoutingCall.respondOutputStream(
    contentType: ContentType? = null,
    status: HttpStatusCode? = null,
    contentLength: Long? = null,
    flow: Flow<String>
) = respondBytesWriter(contentType, status, contentLength, flow::toByteWriteChannel)

@OptIn(InternalSerializationApi::class)
public suspend fun <T : Any> RoutingCall.respondOutputStream(
    contentType: ContentType? = null,
    status: HttpStatusCode? = null,
    contentLength: Long? = null,
    flow: Flow<T>
) {

    val contentType = contentType ?: ContentType.Application.JsonStream

    val suffixContentType = ContentType.Application.Json

    val suitableConverter = application.converters(suffixContentType)?.firstOrNull()

    if (suitableConverter == null) {
        respondOutputStream(
            contentType,
            status,
            contentLength,
            flow.map { value -> Json.Default.encodeToString(value::class.serializer(), value) },
        )
    }
    else {
        respondOutputStream(
            contentType,
            status,
            contentLength,
            flow.map { value ->

                suitableConverter.serialize(suffixContentType, Charsets.UTF_8, TypeInfo(value::class), value)
                ""
            },
        )
    }
}
