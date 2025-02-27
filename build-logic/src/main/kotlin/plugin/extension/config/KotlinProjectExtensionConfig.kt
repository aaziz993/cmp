package plugin.extension.config

import KOTLIN_JVM_TOOLCHAIN_VERSION
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun Project.configureKotlinProjectExtension(extension: KotlinProjectExtension): KotlinProjectExtension =
    extension.apply {
        explicitApi()

        jvmToolchain(KOTLIN_JVM_TOOLCHAIN_VERSION)

        sourceSets.all {
            languageSettings {
                progressiveMode = true
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlin.experimental.ExperimentalObjCName")
                enableLanguageFeature("ExplicitBackingFields")
            }
        }
    }

internal fun Int.toJvmTarget(): JvmTarget =
    JvmTarget.valueOf("JVM_${if (this > 8) "$this" else "1_$this"}")
