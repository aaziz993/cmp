package ai.tech.core.misc.plugin.conditionalheaders

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.conditionalheaders.model.config.ConditionalHeadersConfig
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.conditionalheaders.*
import java.io.File
import java.util.*

public fun Application.configureConditionalHeaders(config: ConditionalHeadersConfig?, block: (io.ktor.server.plugins.conditionalheaders.ConditionalHeadersConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.conditionalheaders.ConditionalHeadersConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            it.versionHeadersPath?.let {
                val file = File(it)
                version { call, outgoingContent ->
                    when (outgoingContent.contentType?.withoutParameters()) {
                        ContentType.Text.CSS -> listOf(
                            EntityTagVersion(file.lastModified().hashCode().toString()),
                            LastModifiedVersion(Date(file.lastModified())),
                        )

                        else -> emptyList()
                    }
                }
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(ConditionalHeaders) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
