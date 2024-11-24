package ai.tech.core.misc.plugin.cors

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.cors.model.config.CORSConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

public fun Application.configureCORS(config: CORSConfig?, block: (io.ktor.server.plugins.cors.CORSConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.cors.CORSConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            it.hosts?.let { it.filter(EnabledConfig::enabled).forEach { allowHost(it.host, it.schemes, it.subDomains) } }
            it.headers?.let { it.forEach { allowHeader(it) } }
            it.methods?.let { it.forEach { allowMethod(it) } }
            it.exposedHeaders?.let { it.forEach { exposeHeader(it) } }
            it.allowCredentials?.let { allowCredentials = it }
            it.maxAgeInSeconds?.let { maxAgeInSeconds = it }
            it.allowSameOrigin?.let { allowSameOrigin = it }
            it.allowNonSimpleContentTypes?.let { allowNonSimpleContentTypes = it }

            // We can also specify options
            /*allowHost("client-host") // Allow requests from client-host
        allowHost("client-host:8081") // Allow requests from client-host on port 8081
        allowHost(
            "client-host",
            subDomains = listOf("en", "de", "es")
        ) // Allow requests from client-host on subdomains en, de and es
        allowHost("client-host", schemes = listOf("http", "https")) // Allow requests from client-host on http and https

        // or methods
        allowMethod(HttpMethod.Put) // Allow PUT method
        allowMethod(HttpMethod.Delete)  // Allow DELETE method*/
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(CORS) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
