package ai.tech.core.misc.model.config.server

import ai.tech.core.misc.model.config.SharedConfig
import ai.tech.core.misc.model.config.di.KoinConfig
import ai.tech.core.misc.type.decode
import com.fasterxml.jackson.databind.SerializationConfig
import io.ktor.server.config.*
import io.ktor.server.plugins.calllogging.CallLoggingConfig
import io.ktor.server.plugins.compression.CompressionConfig
import io.ktor.server.plugins.cors.CORSConfig
import io.ktor.server.plugins.statuspages.StatusPagesConfig
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
