package ai.tech.core.misc.plugin.httpsredirect

import ai.tech.core.misc.plugin.httpsredirect.model.config.HTTPSRedirectConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.httpsredirect.*

public fun Application.configureHttpsRedirect(
    config: HTTPSRedirectConfig?,
    port: Int?,
) = config?.takeIf { it.enable != false }?.let {
    install(HttpsRedirect) {
        // The port to redirect to. By default, 443, the default HTTPS port.
        it.sslPort?.let { sslPort = it } ?: port?.let { sslPort = it }

        // 301 Moved Permanently, or 302 Found redirect.
        it.permanentRedirect?.let { permanentRedirect = it }

        it.excludePrefix?.let { excludePrefix(it) }

        it.excludeSuffix?.let { excludeSuffix(it) }
    }
}
