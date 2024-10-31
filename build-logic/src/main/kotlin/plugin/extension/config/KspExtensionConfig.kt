package plugin.extension.config

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.extension
import plugin.extension.settings

internal fun Project.configKspExtension(extension: KspExtension): KspExtension =
    extension.apply {
        // 0 - Turn off all Ktorfit related error checking, 1 - Check for errors, 2 - Turn errors into warnings
        arg("Ktorfit_Errors", "1")
        // Compile Safety - check your Koin config at compile time (since 1.3.0)
        arg("KOIN_CONFIG_CHECK", "true")
        arg("KOIN_DEFAULT_MODULE", "false")
        // to generate viewModel Koin definition with org.koin.compose.viewmodel.dsl.viewModel instead of regular org.koin.androidx.viewmodel.dsl.viewModel
        arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
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
