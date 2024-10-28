package plugin.extension.config

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import plugin.extension.settings

internal fun Project.configSpotlessExtension(extension: SpotlessExtension) =
    extension.apply {
        settings.config.applyTo("spotless", this)
    }
