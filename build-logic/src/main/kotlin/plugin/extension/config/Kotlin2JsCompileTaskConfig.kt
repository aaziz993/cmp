package plugin.extension.config

import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

internal fun configureKotlin2JsCompileTask(task: Kotlin2JsCompile) =
    task.apply {
        compilerOptions {
            this.target.set("es2015")

            freeCompilerArgs.addAll(
                "-Xdont-warn-on-error-suppression",
            )

            // TODO: Enable after resolving
            //  https://youtrack.jetbrains.com/issue/KT-67355
            /*
            freeCompilerArgs.addAll(
                "-Xir-generate-inline-anonymous-functions",
            )
             */
        }
    }
