package plugin.extension.config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import plugin.extension.settings
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

internal fun Project.configureAllOpenExtension(extension: AllOpenExtension): AllOpenExtension =
    extension.apply {
        annotation("javax.ws.rs.Path")
        annotation("javax.enterprise.context.ApplicationScoped")
        annotation("javax.persistence.Entity")
        annotation("${settings.extension.projectGroup}.core.misc.type.annotation.AllOpen")
    }
