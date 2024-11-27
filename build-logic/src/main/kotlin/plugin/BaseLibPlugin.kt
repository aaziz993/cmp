package plugin

import com.android.build.gradle.LibraryExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import plugin.multiplatform.extension.config.android.configureLibraryExtension
import plugin.extension.config.configureMavenPublishBaseExtension
import plugin.extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import plugin.extension.config.configurePublishingExtension

internal class BaseLibPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply("maven-publish")
            apply(id("vanniktech.maven.publish"))
        }

        // Android library extension
        extensions.configure<LibraryExtension>(::configureLibraryExtension)

        // Publishing
        extensions.configure<PublishingExtension>(::configurePublishingExtension)
        extensions.configure<MavenPublishBaseExtension>(::configureMavenPublishBaseExtension)
    }
}
