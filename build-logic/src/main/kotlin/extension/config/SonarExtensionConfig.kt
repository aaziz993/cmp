package extension.config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import org.sonarqube.gradle.SonarExtension
import extension.settings

internal fun Project.configSonarExtension(extension: SonarExtension) =
    extension.apply {
        settings.config.applyTo("sonar", this)
    }
