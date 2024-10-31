package plugin.cmp.extension.config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension

public fun Project.configComposeExtension(extension: ComposeExtension): ComposeExtension =
    extension.apply {
    }

public fun Project.configComposeAppExtension(extension: ComposeExtension): ComposeExtension =
    extension.apply {
        extensions.configure<DesktopExtension>(::configDesktopExtension)
    }