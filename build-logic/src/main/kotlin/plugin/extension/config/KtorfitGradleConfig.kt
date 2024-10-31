package plugin.extension.config

import de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration
import org.gradle.api.Project

internal fun Project.configKtorfitGradle(configuration: KtorfitGradleConfiguration): KtorfitGradleConfiguration =
    configuration.apply {
    }
