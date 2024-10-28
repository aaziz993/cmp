package plugin.extension.config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension
import plugin.extension.settings

internal fun Project.configNoArgExtension(extension: NoArgExtension): NoArgExtension =
    extension.apply {
        annotation("core.misc.type.annotation.NoArg")
        settings.config.applyTo("noarg", this)
    }
