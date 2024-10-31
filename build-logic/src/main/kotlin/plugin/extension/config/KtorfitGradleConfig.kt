package plugin.extension.config

import de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration
import org.gradle.api.Project

internal fun Project.configureKtorfitGradle(configuration: KtorfitGradleConfiguration): KtorfitGradleConfiguration =
    configuration.apply {
    }
