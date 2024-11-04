package ai.tech.core.misc.plugin.cachingheaders

import ai.tech.core.misc.plugin.cachingheaders.model.config.CachingHeadersConfig
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

public fun Application.configureCachingHeaders(config: CachingHeadersConfig?, block: (io.ktor.server.plugins.cachingheaders.CachingHeadersConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.cachingheaders.CachingHeadersConfig.() -> Unit)? = config?.takeIf { it.enable != false }?.let {
        {
            options { call, outgoingContent ->
                it.rootOption?.let { CachingOptions(it.cacheControl()) }
                val contentType = outgoingContent.contentType?.withoutParameters()
                it.options?.find { it.contentType == contentType }
                    ?.let { CachingOptions(it.cacheControl.cacheControl()) }
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(CachingHeaders) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
