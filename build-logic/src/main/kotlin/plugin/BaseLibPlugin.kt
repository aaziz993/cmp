package plugin

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import extension.config.configPublishExtension

public class BaseLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(id("vanniktech.maven.publish"))
        }

        // Publishing
        extensions.configure<MavenPublishBaseExtension>(::configPublishExtension)
    }
}