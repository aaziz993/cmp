package plugin.extension.config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import plugin.extension.settings
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

internal fun Project.configAllOpenExtension(extension: AllOpenExtension): AllOpenExtension =
    extension.apply {
        annotation("javax.ws.rs.Path")
        annotation("javax.enterprise.context.ApplicationScoped")
        annotation("javax.persistence.Entity")
        annotation("${settings.config.group}.core.misc.type.annotation.AllOpen")
        settings.config.applyTo("allopen", this)
    }
