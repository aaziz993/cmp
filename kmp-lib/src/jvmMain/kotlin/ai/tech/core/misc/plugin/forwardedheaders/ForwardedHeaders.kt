package ai.tech.core.misc.plugin.forwardedheaders

import ai.tech.core.misc.plugin.forwardedheaders.mode.config.ForwardedHeadersConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedheaders.*

public fun Application.configureForwardedHeaders(config: ForwardedHeadersConfig?) = config?.takeIf { it.enable != false }?.let {
    // WARNING: for security, do not include this if not behind a reverse proxy
    install(ForwardedHeaders) {
        it.useFirst?.let {
            if (it) {
                useFirstValue()
            }
        }

        it.useLast?.let {
            if (it) {
                useLastValue()
            }
        }

        it.skipLastProxies?.let { skipLastProxies(it) }

        it.skipKnownProxies?.let { skipKnownProxies(it) }
    }

    // WARNING: for security, do not include this if not behind a reverse proxy
    install(XForwardedHeaders) {

        it.xForwardedHostHeaders?.let { hostHeaders + it }
        it.xForwardedProtoHeaders?.let { protoHeaders + it }
        it.xForwardedForHeaders?.let { forHeaders + it }
        it.xForwardedHttpsFlagHeaders?.let { httpsFlagHeaders + it }
        it.xForwardedPortHeaders?.let { portHeaders + it }

        it.useFirst?.let {
            if (it) {
                useFirstProxy()
            }
        }

        it.useFirst?.let {
            if (it) {
                useLastProxy()
            }
        }

        it.skipLastProxies?.let { skipLastProxies(it) }

        it.skipKnownProxies?.let { skipKnownProxies(it) }
    }
}
