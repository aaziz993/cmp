package plugin.java.extension.config

import extension.settings
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.kotlin.dsl.config
import org.gradle.kotlin.dsl.extra

public fun Project.configJavaApp(javaApplication: JavaApplication): JavaApplication =
    javaApplication.apply {
        mainClass.set("${settings.config.group}.ApplicationKt")
        applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")

        settings.config.applyTo("java.app", this)
    }
