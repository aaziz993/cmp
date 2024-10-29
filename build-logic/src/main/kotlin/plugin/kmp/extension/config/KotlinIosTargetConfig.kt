package plugin.kmp.extension.config

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

internal fun Project.configKotlinIosTarget(target: KotlinNativeTarget) =
    target.apply {
        binaries.framework {
            baseName =
                this@configKotlinIosTarget
                    .path
                    .split(":")
                    .drop(1)
                    .joinToString("-")
            isStatic = true
            // Add it to avoid sqllite3 issues in iOS
            linkerOpts.add("-lsqlite3")
        }
    }
