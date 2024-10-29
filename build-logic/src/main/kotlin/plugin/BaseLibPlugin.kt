package plugin

import com.android.build.gradle.LibraryExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import extension.config.configComposeAndroidLibExtension
import extension.config.configPublishExtension
import extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class BaseLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(id("vanniktech.maven.publish"))
        }

        // Andoird base library extension
        extensions.configure<LibraryExtension>(::configComposeAndroidLibExtension)

        // Publishing
        extensions.configure<MavenPublishBaseExtension>(::configPublishExtension)
    }
}