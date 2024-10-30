package plugin.extension.config

import JAVA_SOURCE_VERSION
import JAVA_TARGET_VERSION
import plugin.extension.settings
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.config

internal fun Project.configJavaPluginExtension(extension: JavaPluginExtension) =
    extension.apply {
        sourceCompatibility = JAVA_SOURCE_VERSION.toJavaVersion()
        targetCompatibility = JAVA_TARGET_VERSION.toJavaVersion()

        settings.config.applyTo("java", this)
    }

internal fun DependencyHandler.implementation(dependency: Any) {
    add(
        JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
        dependency,
    )
}

internal fun DependencyHandler.debugImplementation(dependency: Any) {
    add(
        "debugImplementation",
        dependency,
    )
}

internal fun DependencyHandler.testImplementation(dependency: Any) {
    add(
        JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
        dependency,
    )
}

internal fun Int.toJavaVersion(): JavaVersion = JavaVersion.toVersion(this)
