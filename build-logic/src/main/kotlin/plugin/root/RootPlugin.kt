package plugin.root

import com.android.build.gradle.internal.cxx.io.writeTextIfDifferent
import com.android.build.gradle.internal.cxx.logging.warnln
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URL
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.extension
import plugin.extension.settings
import plugin.root.model.ProjectFileOverrideType

public class RootPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(settings.extension) {
            group = projectGroup
            version = projectVersion
            subprojects.forEach { project ->
                project.group = "$projectGroup.${project.name.replace("[-_]".toRegex(), ".")}"
                project.version = projectVersion
            }

            tasks.create("downloadProjectFiles", Task::class.java) {
                // Download and write to file license
                downloadProjectFile(
                    providers.gradleProperty("project.license.text.url").get(),
                    providers.gradleProperty("project.license.fallback.file").get(),
                    "LICENSE",
                    ProjectFileOverrideType.valueOf(
                        providers.gradleProperty("project.license.file.override").get().uppercase(),
                    ),
                    mapOf(
                        providers.gradleProperty("project.license.year.placeholder").get() to projectInceptionYear,
                        providers.gradleProperty("project.license.owner.placeholder").get() to developerName,
                    ),
                )

                // Download or fallback to file and write to file code of conduct
                downloadProjectFile(
                    providers.gradleProperty("project.code.of.conduct.text.url").get(),
                    providers.gradleProperty("project.code.of.conduct.fallback.file").get(),
                    "CODE_OF_CONDUCT.md",
                    ProjectFileOverrideType.valueOf(
                        providers.gradleProperty("project.code.of.conduct.file.override").get().uppercase(),
                    ),
                )

                // Download or fallback to file and write to file contributing
                downloadProjectFile(
                    providers.gradleProperty("project.contributing.text.url").get(),
                    providers.gradleProperty("project.contributing.fallback.file").get(),
                    "CONTRIBUTING.md",
                    ProjectFileOverrideType.valueOf(
                        providers.gradleProperty("project.contributing.file.override").get().uppercase(),
                    ),
                )
            }
        }
    }
}

private fun downloadProjectFile(
    url: String,
    fallbackFile: String,
    destFile: String,
    overrideType: ProjectFileOverrideType,
    placeholders: Map<String, String> = emptyMap()
) {
    val text = placeholders.entries.fold(
        try {
            URI(url).toURL().readText()
        }
        catch (_: IOException) {
            val file = File(fallbackFile)
            warnln(
                "Cannot retrieve ${file.nameWithoutExtension} from \"$url\" fallback to file \"$fallbackFile\"",
            )
            file.readText()
        },
    ) { acc, (k, v) -> acc.replace(k, v) }

    val file = File(destFile)
    if (file.exists()) {
        when (overrideType) {
            ProjectFileOverrideType.NEVER -> return

            ProjectFileOverrideType.IF_DIFFERENCE -> if (file.readText() != text) {
                file.writeText(text)
            }

            else -> Unit
        }
    }
    file.writeText(text)
}
