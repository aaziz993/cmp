package plugin.kmp.extension.config

import KOTLIN_JVM_TARGET_VERSION
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import plugin.extension.config.toJvmTarget

internal fun Project.configureKotlinAndroidTarget(target: KotlinAndroidTarget) =
    target.apply {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(KOTLIN_JVM_TARGET_VERSION.toJvmTarget())
        }
    }
