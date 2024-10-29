package extension.config

import KOTLIN_JVM_TOOLCHAIN_VERSION
import extension.settings
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

public fun Project.configKotlinProjectExtension(extension: KotlinProjectExtension): KotlinProjectExtension =
    extension.apply {
        explicitApi()

        jvmToolchain(KOTLIN_JVM_TOOLCHAIN_VERSION)

        sourceSets.configureEach {
            languageSettings {
                progressiveMode = true
            }
        }
        
        settings.config.applyTo("kotlin", this)
    }

internal fun Int.toJvmTarget(): JvmTarget =
    JvmTarget.valueOf("JVM_${if (this > 8) "$this" else "1_$this"}")
