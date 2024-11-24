package plugin.extension.config.spotless

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import plugin.extension.config.spotless.model.Format
import plugin.extension.settings

internal fun Project.configureSpotlessExtension(extension: SpotlessExtension) =
    extension.apply {
        with(settings.extension) {
            val versionCatalogVersions = versionCatalog.getTable("versions")!!

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

            fun getProjectLicenseHeaderText(file: String) = layout.projectDirectory.file(
                providers.gradleProperty(file).get(),
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
                // Use the default importOrder configuration
                importOrder()
                // Adds the ability to have spotless ignore specific portions of a project. The usage looks like the following
                toggleOffOn()
                // Tells spotless to format according to the Google Style Guide
                // (https://google.github.io/styleguide/javaguide.html)
                googleJavaFormat(versionCatalogVersions.getString("google-java-format")).aosp().reflowLongStrings().formatJavadoc(false).reorderImports(false).groupArtifact("com.google.googlejavaformat:google-java-format")
                // fixes formatting of type annotations
                formatAnnotations()
                // Will remove any unused imports from any of your Java classes
                removeUnusedImports()
                // Cleanthat will refactor your code, but it may break your style: apply it before your formatter
                cleanthat()          // has its own section below
                // Will remove any extra whitespace at the end of lines
                trimTrailingWhitespace()
                // Will add a newline character to the end of files content
                endWithNewline()
                // Specifies license header text
                licenseHeader(projectJavaFilesLicenseHeaderText("project.java.files.license.header.text.path"))
            }

            // Configuration for Kotlin files
            kotlin {
                // Include source files
                target("**/*.kt")
                // Exclude source files
                targetExclude(*excludeSourceFileTargets.toTypedArray())
                // Adds the ability to have spotless ignore specific portions of a project. The usage looks like the following
                toggleOffOn()
                // Use ktlint with version and custom .editorconfig
                ktlint(versionCatalogVersions.getString("ktlint"))
                    .setEditorConfigPath(providers.gradleProperty("spotless.editor.config.file").get())
                // Will remove any extra whitespace at the end of lines
                trimTrailingWhitespace()
                // Will add a newline character to the end of files content
                endWithNewline()
                // Specifies license header text
                licenseHeader(projectJavaFilesLicenseHeaderText("project.kt.files.license.header.text.path"))
            }

            // Common configuration for miscellaneous files
            listOf(
                Format(
                    "kts",
                    listOf("kts"),
                    projectJavaFilesLicenseHeaderText("project.kt.files.license.header.text.path"),
                    providers.gradleProperty("project.kts.files.license.header.text.delimiter").get(),
                ),
                Format(
                    "xml",
                    listOf("xml"),
                    "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n${
                        projectHtmlFilesLicenseHeaderText("project.xml.files.license.header.text.path")
                    }",
                    providers.gradleProperty("project.xml.files.license.header.text.delimiter").get(),
                ),
                Format(
                    "yaml",
                    listOf("yaml", "yml"),
                    projectYamlFilesLicenseHeaderText("project.yaml.files.license.header.text.path"),
                    providers.gradleProperty("project.yaml.files.license.header.text.delimiter").get(),
                ),
                Format(
                    "properties",
                    listOf("properties"),
                    projectYamlFilesLicenseHeaderText("project.properties.files.license.header.text.path"),
                    providers.gradleProperty("project.properties.files.license.header.text.delimiter").get(),
                ),
                Format(
                    "html",
                    listOf("html"),
                    projectHtmlFilesLicenseHeaderText("project.html.files.license.header.text.path"),
                    providers.gradleProperty("project.html.files.license.header.text.delimiter").get(),
                ),
                Format(
                    "md",
                    listOf("md"),
                    projectHtmlFilesLicenseHeaderText("project.md.files.license.header.text.path"),
                    providers.gradleProperty("project.md.files.license.header.text.delimiter").get(),
                ),
                Format(
                    "gitignore",
                    listOf("gitignore"),
                    projectYamlFilesLicenseHeaderText("project.gitignore.files.license.header.text.path"),
                    providers.gradleProperty("project.gitignore.files.license.header.text.delimiter").get(),
                ),
                Format(
                    "gitattributes",
                    listOf("gitattributes"),
                    projectYamlFilesLicenseHeaderText("project.gitattributes.files.license.header.text.path"),
                    providers.gradleProperty("project.gitattributes.files.license.header.text.delimiter").get(),
                ),
            ).forEach {
                format(it.name) {
                    // Include source files
                    target(*it.formats.map { "**/*.$it" }.toTypedArray())
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
                    licenseHeader(it.licenseHeaderPath, it.delimiter)
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
                ktlint(versionCatalogVersions.getString("ktlint"))
            }
        }
    }


