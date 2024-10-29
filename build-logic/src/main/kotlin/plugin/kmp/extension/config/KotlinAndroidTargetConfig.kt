package plugin.kmp.extension.config

import extension.config.toJvmTarget
import KOTLIN_JVM_TARGET_VERSION
import extension.settings
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget

internal fun Project.configKotlinAndroidTarget(target: KotlinAndroidTarget) =
    target.apply {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(KOTLIN_JVM_TARGET_VERSION.toJvmTarget())
        }

        settings.config.applyTo("kotlin.android.target", this)
    }
