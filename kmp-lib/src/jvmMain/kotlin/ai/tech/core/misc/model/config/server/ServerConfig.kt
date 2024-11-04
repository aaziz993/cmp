package ai.tech.core.misc.model.config.server

import ai.tech.core.misc.auth.server.model.config.ServerAuthConfig
import ai.tech.core.misc.model.config.SharedConfig
import ai.tech.core.misc.model.config.di.KoinConfig
import ai.tech.core.misc.plugin.calllogging.model.config.CallLoggingConfig
import ai.tech.core.misc.plugin.compression.model.config.CompressionConfig
import ai.tech.core.misc.plugin.cors.model.config.CORSConfig
import ai.tech.core.misc.plugin.routing.model.config.RoutingConfig
import ai.tech.core.misc.plugin.serialization.model.config.SerializationConfig
import ai.tech.core.misc.plugin.statuspages.model.config.StatusPagesConfig
import ai.tech.core.misc.type.decode
import io.ktor.server.config.*
import kotlinx.serialization.json.Json

public val appConfigJson: Json = Json {
    ignoreUnknownKeys = true
}

public interface ServerConfig : SharedConfig {

    public val koin: KoinConfig?
    public val serialization: SerializationConfig?
    public val compression: CompressionConfig?
    public val callLogging: CallLoggingConfig?
    public val cors: CORSConfig?
    public val statusPages: StatusPagesConfig?
    public val auth: ServerAuthConfig?
    public val routing: RoutingConfig?
}

public inline fun <reified T : Any> ApplicationConfig.decodeConfig(key: String): T? =
    if (keys().any { it.startsWith("$key.") }) {
        appConfigJson.decode(config(key).toMap())
    }
    else {
        null
    }
