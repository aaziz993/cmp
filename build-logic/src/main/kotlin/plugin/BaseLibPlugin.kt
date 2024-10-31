package plugin

import com.android.build.gradle.LibraryExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import plugin.extension.config.configLibraryExtension
import plugin.extension.config.configMavenPublishBaseExtension
import plugin.extension.config.configMavenPublication
import plugin.extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure

internal class BaseLibPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
//            apply("maven-publish")
            apply(id("vanniktech.maven.publish"))
        }

        // Android library extension
        extensions.configure<LibraryExtension>(::configLibraryExtension)

        // Publishing
//        extensions.configure<MavenPublication>(::configMavenPublication)

        extensions.configure<MavenPublishBaseExtension>(::configMavenPublishBaseExtension)
    }
}
