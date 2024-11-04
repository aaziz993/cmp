package ai.tech.core.misc.plugin.partialcontent

import ai.tech.core.misc.plugin.partialcontent.model.config.PartialContentConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.partialcontent.*

public fun Application.configurePartialContent(config: PartialContentConfig?, block: (io.ktor.server.plugins.partialcontent.PartialContentConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.partialcontent.PartialContentConfig.() -> Unit)? = config?.takeIf { it.enable != false }?.let {
        {
            // Maximum number of ranges that will be accepted from an HTTP request.
            // If the HTTP request specifies more ranges, they will all be merged into a single range.
            config.maxRangeCount?.let { maxRangeCount = it }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(PartialContent) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
