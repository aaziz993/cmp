package plugin.java.extension.config

import plugin.extension.settings
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.kotlin.dsl.extension
import org.gradle.kotlin.dsl.extra

internal fun Project.configJavaApp(javaApplication: JavaApplication): JavaApplication =
    javaApplication.apply {
        mainClass.set("${settings.extension.projectGroup}.ApplicationKt")
        applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
    }
