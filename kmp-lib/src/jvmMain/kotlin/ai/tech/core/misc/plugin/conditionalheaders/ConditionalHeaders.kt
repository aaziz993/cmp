package ai.tech.core.misc.plugin.conditionalheaders

import ai.tech.core.misc.plugin.conditionalheaders.model.config.ConditionalHeadersConfig
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.conditionalheaders.*
import java.io.File
import java.util.*

public fun Application.configureConditionalHeaders(config: ConditionalHeadersConfig?) = config?.takeIf { it.enable != false }?.let {
    install(ConditionalHeaders) {
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
