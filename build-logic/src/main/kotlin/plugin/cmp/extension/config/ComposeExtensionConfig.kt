package plugin.cmp.extension.config

import extension.settings
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension

internal fun Project.configComposeExtension(extension: ComposeExtension): ComposeExtension =
    extension.apply {

        settings.config.applyTo("compose", this)
    }
