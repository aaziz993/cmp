package ai.tech.core.misc.plugin.forwardedheaders

import ai.tech.core.misc.plugin.forwardedheaders.mode.config.ForwardedHeadersConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedheaders.*

public fun Application.configureForwardedHeaders(
    config: ForwardedHeadersConfig?,
    block: (io.ktor.server.plugins.forwardedheaders.ForwardedHeadersConfig.() -> Unit)? = null,
) {
    var configBlock: (io.ktor.server.plugins.forwardedheaders.ForwardedHeadersConfig.() -> Unit)? = config?.takeIf { it.enable != false }?.let {
        {
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
    }

    if (configBlock == null && block == null) {
        return
    }

    // WARNING: for security, do not include this if not behind a reverse proxy
    install(ForwardedHeaders) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
