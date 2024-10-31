package plugin.root

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import plugin.extension.settings

public class RootPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            settings.extension.let { config ->
                group = config.projectGroup
                version = config.projectVersion
                subprojects.forEach { project ->
                    project.group = "${config.projectGroup}.${project.name.replace("[-_]".toRegex(), ".")}"
                    project.version = config.projectVersion
                }
            }
        }
}
