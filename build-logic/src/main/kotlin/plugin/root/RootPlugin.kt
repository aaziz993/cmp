package plugin.root

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import plugin.extension.settings

public class RootPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            settings.config.let { config ->
                group = config.group
                version = config.version
                subprojects.forEach { project ->
                    project.group = "${config.group}.${project.name.replace("[-_]".toRegex(), ".")}"
                    project.version = config.version
                }
            }
        }
}
