package ai.tech.core.misc.plugin.templating

import ai.tech.core.misc.plugin.templating.model.config.FreeMarkerConfig
import freemarker.cache.ClassTemplateLoader
import freemarker.cache.FileTemplateLoader
import freemarker.cache.MultiTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import java.io.File

public fun Application.configureFreeMarker(config: FreeMarkerConfig?) =
    config?.takeIf { it.enable != false }?.let {
        install(FreeMarker) {
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
