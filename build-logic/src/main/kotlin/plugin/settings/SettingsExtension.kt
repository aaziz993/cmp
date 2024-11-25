@file:Suppress("PackageDirectoryMismatch")

package org.gradle.kotlin.dsl

import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware
import plugin.settings.SettingsPluginExtension

/**
 * DSL entry point for the git hooks commits.
 * This function is needed because Gradle doesn't generate accessors for settings extensions.
 */
public fun Settings.config(configure: SettingsPluginExtension.() -> Unit) {
    extensions.getByType<SettingsPluginExtension>().configure()
}

internal val Settings.extension: SettingsPluginExtension
    get() =
        (this as ExtensionAware).extensions.getByName(SettingsPluginExtension.NAME) as SettingsPluginExtension
