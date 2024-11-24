package ai.tech.core.misc.plugin.forwardedheaders

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.forwardedheaders.mode.config.XForwardedHeadersConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedheaders.*

public fun Application.configureXForwardedHeaders(
    config: XForwardedHeadersConfig?,
    block: (io.ktor.server.plugins.forwardedheaders.XForwardedHeadersConfig.() -> Unit)? = null,
) {
    var configBlock: (io.ktor.server.plugins.forwardedheaders.XForwardedHeadersConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            it.hostHeaders?.let { hostHeaders + it }
            it.protoHeaders?.let { protoHeaders + it }
            it.forHeaders?.let { forHeaders + it }
            it.httpsFlagHeaders?.let { httpsFlagHeaders + it }
            it.portHeaders?.let { portHeaders + it }

            it.useFirst?.let {
                if (it) {
                    useFirstProxy()
                }
            }

            it.useLast?.let {
                if (it) {
                    useLastProxy()
                }
            }

            it.skipLastProxies?.let { skipLastProxies(it) }

            it.skipKnownProxies?.let { skipKnownProxies(it) }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    // WARNING: for security, do not include this if not behind a reverse proxy
    install(XForwardedHeaders) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
