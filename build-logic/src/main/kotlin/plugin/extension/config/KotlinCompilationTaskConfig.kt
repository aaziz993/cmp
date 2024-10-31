package plugin.extension.config

import ALL_WARNINGS_AS_ERRORS
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import plugin.extension.settings

internal fun Project.configKotlinCompilationTask(task: KotlinCompilationTask<*>) = task.apply {
    with(settings.extension) {
        compilerOptions {
            allWarningsAsErrors.set(this@with.allWarningsAsErrors)

            freeCompilerArgs.addAll(
                "-opt-in=kotlin.ExperimentalStdlibApi",
                "-opt-in=kotlin.ExperimentalUnsignedTypes",
                "-opt-in=kotlin.contracts.ExperimentalContracts",
            )
        }
    }
}