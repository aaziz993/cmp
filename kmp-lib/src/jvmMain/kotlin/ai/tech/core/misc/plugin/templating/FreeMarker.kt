package ai.tech.core.misc.plugin.templating

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.templating.model.config.FreeMarkerConfig
import freemarker.cache.ClassTemplateLoader
import freemarker.cache.FileTemplateLoader
import freemarker.cache.MultiTemplateLoader
import freemarker.template.Configuration
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.sessions.SessionsConfig
import java.io.File

public fun Application.configureFreeMarker(config: FreeMarkerConfig?, block: (Configuration.() -> Unit)? = null) {
    val configBlock: (Configuration.() -> Unit)? = config?.takeIf(EnabledConfig::enable)?.let {
        {
            val templateLoaders = (it.classPaths?.let {
                it.map { ClassTemplateLoader(this::class.java.classLoader, it) }
            } ?: emptyList()) +
                (it.filePaths?.let {
                    it.map { FileTemplateLoader(File(it)) }
                } ?: emptyList())

            if (templateLoaders.isNotEmpty()) {
                templateLoader =
                    MultiTemplateLoader(templateLoaders.toTypedArray())
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(FreeMarker) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
