package extension.config

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.config
import extension.settings

internal fun Project.configKspExtension(extension: KspExtension): KspExtension =
    extension.apply {
        settings.config.applyTo("ksp", this)
    }

internal fun DependencyHandler.ksp(dependencyNotation: Any) {
    add("ksp", dependencyNotation)
}

internal fun DependencyHandler.kspCommonMainMetadata(dependencyNotation: Any) {
    add("kspCommonMainMetadata", dependencyNotation)
}

internal fun DependencyHandler.kspJs(dependencyNotation: Any) {
    add("kspJs", dependencyNotation)
}

internal fun DependencyHandler.kspJvm(dependencyNotation: Any) {
    add("kspJvm", dependencyNotation)
}

internal fun DependencyHandler.kspAndroid(dependencyNotation: Any) {
    add("kspAndroid", dependencyNotation)
}

internal fun DependencyHandler.kspIosX64(dependencyNotation: Any) {
    add("kspIosX64", dependencyNotation)
}

internal fun DependencyHandler.kspIosArm64(dependencyNotation: Any) {
    add("kspIosArm64", dependencyNotation)
}

internal fun DependencyHandler.kspIosSimulatorArm64(dependencyNotation: Any) {
    add("kspIosSimulatorArm64", dependencyNotation)
}
