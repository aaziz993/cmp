package extension.config

import ALL_WARNINGS_AS_ERRORS
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

internal fun Project.configKotlinCompilationTask(task: KotlinCompilationTask<*>) = task.apply {
    compilerOptions {
        allWarningsAsErrors.set(ALL_WARNINGS_AS_ERRORS)

        freeCompilerArgs.addAll(
            "-opt-in=kotlin.ExperimentalStdlibApi",
            "-opt-in=kotlin.ExperimentalUnsignedTypes",
            "-opt-in=kotlin.contracts.ExperimentalContracts",
        )
    }
}