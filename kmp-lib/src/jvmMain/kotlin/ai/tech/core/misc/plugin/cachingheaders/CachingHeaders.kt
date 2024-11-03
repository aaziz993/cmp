package ai.tech.core.misc.plugin.cachingheaders

import ai.tech.core.misc.plugin.cachingheaders.model.config.CachingHeadersConfig
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

public fun Application.configureureCachingHeaders(config: CachingHeadersConfig?) = config?.takeIf { it.enable != false }?.let {
    install(CachingHeaders) {
        options { call, outgoingContent ->
            it.rootOption?.let { CachingOptions(it.cacheControl()) }
            val contentType = outgoingContent.contentType?.withoutParameters()
            it.options?.find { it.contentType == contentType }
                ?.let { CachingOptions(it.cacheControl.cacheControl()) }
        }
    }
}
