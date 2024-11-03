package plugin.extension.config

import KOTLIN_JVM_TARGET_VERSION
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget

internal fun Project.configureKotlinAndroidTarget(target: KotlinAndroidTarget) =
    target.apply {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(
                providers.gradleProperty("android.compilations.kotlin.options.jvm.target").getOrElse(KOTLIN_JVM_TARGET_VERSION.toString()).toInt()
                    .toJvmTarget(),
            )
        }
    }
