package plugin.multiplatform.extension.config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

internal fun Project.configureKotlinNativeTarget(target: KotlinNativeTarget) =
    target.apply {
        compilations["main"].compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
                freeCompilerArgs.add("-Xallocator=custom")
                freeCompilerArgs.add("-XXLanguage:+ImplicitSignedToUnsignedIntegerConversion")
                freeCompilerArgs.add("-Xadd-light-debug=enable")

                freeCompilerArgs.addAll(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-opt-in=kotlin.time.ExperimentalTime",
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-opt-in=kotlinx.coroutines.FlowPreview",
                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                    "-opt-in=kotlinx.cinterop.BetaInteropApi",
                )
            }
        }
    }
