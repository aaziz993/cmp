package ai.tech.core.misc.plugin.websockets

import ai.tech.core.misc.model.config.EnabledConfig
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
    wsURL: String,
    block: (WebSockets.WebSocketOptions.() -> Unit)? = null
) {
    var configBlock: (WebSockets.WebSocketOptions.() -> Unit)? = null

    config?.takeIf(EnabledConfig::enabled)?.let {
        // Websocket page for testing
        routing {
            it.page?.filter(EnabledConfig::enabled)?.forEach {
                get(it.uri) {
                    call.respond(
                        FreeMarkerContent(
                            "websocket/index.ftl",
                            mapOf(
                                "wsURL" to "$wsURL/${it.wsURI}",
                            ),
                        ),
                    )
                }
            }
        }
        configBlock = {
            it.pingPeriod?.let { pingPeriod = it }
            it.timeout?.let { timeout = it }
            it.maxFrameSize?.let { maxFrameSize = it }
            it.masking?.let { masking = it }
            // Configure WebSockets
            // Serializer for WebSockets
            it.contentConverter?.takeIf(EnabledConfig::enabled)?.let {
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
    }

    if (configBlock == null && block == null) {
        return
    }

    install(WebSockets) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
