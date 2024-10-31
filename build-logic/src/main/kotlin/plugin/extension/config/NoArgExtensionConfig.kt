package plugin.extension.config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension
import plugin.extension.settings

internal fun Project.configNoArgExtension(extension: NoArgExtension): NoArgExtension =
    extension.apply {
        annotation("${settings.extension.projectGroup}.core.misc.type.annotation.NoArg")
    }
