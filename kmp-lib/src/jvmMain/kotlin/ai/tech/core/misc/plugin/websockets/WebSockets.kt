package ai.tech.core.misc.plugin.websockets

import ai.tech.core.misc.plugin.websockets.model.config.WebSocketsConfig
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json

public fun Application.configureWebSockets(
    config: WebSocketsConfig?,
    websocketPageUrl: String,
) = config?.takeIf { it.enable != false }?.let {
    install(WebSockets) {
        it.pingPeriod?.let { pingPeriod = it }
        it.timeout?.let { timeout = it }
        it.maxFrameSize?.let { maxFrameSize = it }
        it.masking?.let { masking = it }
        // Configure WebSockets
        // Serializer for WebSockets
        it.contentConverter?.let {
            contentConverter = KotlinxWebsocketSerializationConverter(
                Json {
                    it.encodeDefaults?.let { encodeDefaults = it }
                    it.explicitNulls?.let { explicitNulls = it }
                    it.ignoreUnknownKeys?.let { ignoreUnknownKeys = it }
                    it.isLenient?.let { isLenient = it }
                    it.allowStructuredMapKeys?.let { allowStructuredMapKeys = it }
                    it.prettyPrint?.let { prettyPrint = it }
                    it.prettyPrintIndent?.let { prettyPrintIndent = it }
                    it.coerceInputValues?.let { coerceInputValues = it }
                    it.useArrayPolymorphism?.let { useArrayPolymorphism = it }
                    it.classDiscriminator?.let { classDiscriminator = it }
                    it.allowSpecialFloatingPointValues?.let { allowSpecialFloatingPointValues = it }
                    it.useAlternativeNames?.let { useAlternativeNames = it }
                    it.decodeEnumsCaseInsensitive?.let { decodeEnumsCaseInsensitive = it }
                },
            )
        }
        // Remember it will close the connection if you don't send a ping in pingPeriod seconds
        // https://ktor.io/docs/websocket.html#configure
    }

    // Websocket page for testing
    it.page?.let { page ->
        routing {
            get(page.uri ?: "/websocket") {
                call.respond(
                    FreeMarkerContent(
                        page.filePath ?: "websocket/index.ftl",
                        mapOf(
                            "baseAddress" to websocketPageUrl,
                        ),
                    ),
                )
            }
        }
    }
}
