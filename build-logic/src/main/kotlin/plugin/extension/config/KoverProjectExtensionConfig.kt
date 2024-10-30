package plugin.extension.config

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import plugin.extension.settings

internal fun Project.configKoverProjectExtension(extension: KoverProjectExtension) =
    extension.apply {
        settings.config.applyTo("kover", this)
    }