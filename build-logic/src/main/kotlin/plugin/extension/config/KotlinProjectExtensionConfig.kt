package plugin.extension.config

import KOTLIN_JVM_TOOLCHAIN_VERSION
import plugin.extension.settings
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun Project.configureKotlinProjectExtension(extension: KotlinProjectExtension): KotlinProjectExtension =
    extension.apply {
        explicitApi()

        jvmToolchain(KOTLIN_JVM_TOOLCHAIN_VERSION)

        sourceSets.configureEach {
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
