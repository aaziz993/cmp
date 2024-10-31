package plugin.extension.config

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import plugin.extension.settings
import java.io.File

internal fun Project.configSpotlessExtension(extension: SpotlessExtension) =
    extension.apply {
        with(settings.extension) {
            lineEndings = LineEnding.UNIX

            val excludeSourceFileTargets = listOf(
                "**/generated-src/**",
                "**/${layout.buildDirectory.get()}/**",
                "**/build-*/**",
                "**/.idea/**",
                "**/.fleet/**",
                "**/.idea/**",
                "**/.gradle/**",
                "/spotless/**",
                "**/resources/**",
                "**/buildSrc/**",
            )

            fun getProjectLicenseHeaderText(file: String) = layout.projectDirectory.file("../${
                providers.gradleProperty(file).get()}"
            ).asFile.readText()
                .replace(
                    providers
                        .gradleProperty("project.license.header.text.file.project.inception.year.placeholder")
                        .get(),
                    projectInceptionYear,
                )
                .replace(
                    providers
                        .gradleProperty("project.license.header.text.file.project.developer.name.placeholder")
                        .get(),
                    developerName,
                )
                .replace(
                    providers
                        .gradleProperty("project.license.header.text.file.project.license.name.placeholder")
                        .get(),
                    projectLicenseName,
                )

            fun projectJavaFilesLicenseHeaderText(file: String) = "/*${
                getProjectLicenseHeaderText(file)
            }*/"

            fun projectHtmlFilesLicenseHeaderText(file: String) = "<!--${
                getProjectLicenseHeaderText(file)
            }-->"

            fun projectYamlFilesLicenseHeaderText(file: String) = "#${
                getProjectLicenseHeaderText(file)
                    .substringBeforeLast("\n").replace("\n", "\n#")
            }"

            // Configuration for Java files
            java {
                // Include source files
                target("**/*.java")
                // Exclude source files
                targetExclude(*excludeSourceFileTargets.toTypedArray())
                // Adds the ability to have spotless ignore specific portions of a project. The usage looks like the following
                toggleOffOn()
                // Tells spotless to format according to the Google Style Guide
                // (https://google.github.io/styleguide/javaguide.html)
                googleJavaFormat()
                // Will remove any unused imports from any of your Java classes
                removeUnusedImports()
                // Will remove any extra whitespace at the end of lines
                trimTrailingWhitespace()
                // Will add a newline character to the end of files content
                endWithNewline()
                // Specifies license header text
                licenseHeader(projectJavaFilesLicenseHeaderText("project.java.files.license.header.text.file"))
            }

            // Configuration for Kotlin files
            kotlin {
                // Include source files
                target("**/*.kt")
                // Exclude source files
                targetExclude(*excludeSourceFileTargets.toTypedArray())
                // Adds the ability to have spotless ignore specific portions of a project. The usage looks like the following
                toggleOffOn()
                // Use ktlint with version 1.2.1 and custom .editorconfig
                ktlint("1.2.1")
                    .setEditorConfigPath(providers.gradleProperty("spotless.editor.config.file").get())
                // Will remove any extra whitespace at the end of lines
                trimTrailingWhitespace()
                // Will add a newline character to the end of files content
                endWithNewline()
                // Specifies license header text
                licenseHeader(projectJavaFilesLicenseHeaderText("project.kt.files.license.header.text.file"))
            }

            // Common configuration for miscellaneous files
            mapOf(
                "kts" to Triple(
                    listOf("kts"),
                    projectJavaFilesLicenseHeaderText("project.kt.files.license.header.text.file"),
                    providers.gradleProperty("project.kts.files.license.header.text.delimiter").get(),
                ),
                "xml" to Triple(
                    listOf("xml"),
                    "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n${
                        projectHtmlFilesLicenseHeaderText("project.xml.files.license.header.text.file")
                    }",
                    providers.gradleProperty("project.xml.files.license.header.text.delimiter").get(),
                ),
                "yaml" to Triple(
                    listOf("yaml", "yml"),
                    projectYamlFilesLicenseHeaderText("project.yaml.files.license.header.text.file"),
                    providers.gradleProperty("project.yaml.files.license.header.text.delimiter").get(),
                ),
                "properties" to Triple(
                    listOf("properties"),
                    projectYamlFilesLicenseHeaderText("project.properties.files.license.header.text.file"),
                    providers.gradleProperty("project.properties.files.license.header.text.delimiter").get(),
                ),
                "html" to Triple(
                    listOf("html"),
                    projectHtmlFilesLicenseHeaderText("project.html.files.license.header.text.file"),
                    providers.gradleProperty("project.html.files.license.header.text.delimiter").get(),
                ),
                "md" to Triple(
                    listOf("md"),
                    projectHtmlFilesLicenseHeaderText("project.md.files.license.header.text.file"),
                    providers.gradleProperty("project.md.files.license.header.text.delimiter").get(),
                ),
                "gitignore" to Triple(
                    listOf("gitignore"),
                    projectYamlFilesLicenseHeaderText("project.gitignore.files.license.header.text.file"),
                    providers.gradleProperty("project.gitignore.files.license.header.text.delimiter").get(),
                ),
                "gitattributes" to Triple(
                    listOf("gitattributes"),
                    projectYamlFilesLicenseHeaderText("project.gitattributes.files.license.header.text.file"),
                    providers.gradleProperty("project.gitattributes.files.license.header.text.delimiter").get(),
                ),
            ).forEach { entry ->
                format(entry.key) {
                    // Include source files
                    target(*entry.value.first.map { "**/*.$it" }.toTypedArray())
                    // Exclude source files
                    targetExclude(*excludeSourceFileTargets.toTypedArray())
                    // Adds the ability to have spotless ignore specific portions of a project.
                    // The usage looks like the following
                    toggleOffOn()
                    // Will remove any extra whitespace at the end of lines
                    trimTrailingWhitespace()
                    // Will add a newline character to the end of files content
                    endWithNewline()
                    // Specifies license header text
                    licenseHeader(entry.value.second, entry.value.third)
                }
            }

            // Configuration for properties files
            format("properties") {
                // Will remove any extra whitespace at the beginning of lines
                indentWithSpaces()
            }

            // Additional configuration for Kotlin Gradle scripts
            kotlinGradle {
                target("*.gradle.kts")
                // Apply ktlint to Gradle Kotlin scripts
                ktlint("1.2.1")
            }
        }
    }