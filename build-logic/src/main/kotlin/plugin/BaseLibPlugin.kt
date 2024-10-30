package plugin

import com.android.build.gradle.LibraryExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import plugin.extension.config.configComposeAndroidLibExtension
import plugin.extension.config.configPublishExtension
import plugin.extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal class BaseLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(id("vanniktech.maven.publish"))
        }

        // Android base library extension
        extensions.configure<LibraryExtension>(::configComposeAndroidLibExtension)

        // Publishing
        extensions.configure<MavenPublishBaseExtension>(::configPublishExtension)
    }
}