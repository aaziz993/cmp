package ai.tech.core.misc.plugin.partialcontent

import ai.tech.core.misc.plugin.partialcontent.model.config.PartialContentConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.partialcontent.*

public fun Application.configurePartialContent(config: PartialContentConfig?) = config?.takeIf { it.enable != false }?.let {
    install(PartialContent) {
        // Maximum number of ranges that will be accepted from an HTTP request.
        // If the HTTP request specifies more ranges, they will all be merged into a single range.
        config.maxRangeCount?.let { maxRangeCount = it }
    }
}
