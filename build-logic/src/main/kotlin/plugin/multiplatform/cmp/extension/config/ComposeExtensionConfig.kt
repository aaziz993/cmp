package plugin.multiplatform.cmp.extension.config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension

public fun Project.configureComposeExtension(extension: ComposeExtension): ComposeExtension =
    extension.apply {
    }

public fun Project.configureComposeAppExtension(extension: ComposeExtension): ComposeExtension =
    extension.apply {
        extensions.configure<DesktopExtension>(::configureDesktopExtension)
    }
