package plugin.cmp.extension.config

import plugin.extension.settings
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import org.jetbrains.compose.ComposeExtension

internal fun Project.configComposeExtension(extension: ComposeExtension): ComposeExtension =
    extension.apply {

        settings.config.applyTo("compose", this)
    }
