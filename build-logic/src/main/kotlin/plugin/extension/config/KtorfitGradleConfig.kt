package plugin.extension.config

import de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration
import plugin.extension.settings
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config

internal fun Project.configKtorfitGradle(configuration: KtorfitGradleConfiguration): KtorfitGradleConfiguration =
    configuration.apply {
        settings.config.applyTo("ktorfit", this)
    }
