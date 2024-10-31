package plugin.extension.config

import org.gradle.api.Project
import org.sonarqube.gradle.SonarExtension

// Project code analysis
// To analyze a project hierarchy, apply the SonarQube plugin to the root project of the hierarchy.
// Typically, (but not necessarily) this will be the root project of the Gradle build.
// Information pertaining to the analysis as a whole has to be configured in the sonar block of this project.
// Any properties set on the command line also apply to this project.
internal fun Project.configSonarExtension(extension: SonarExtension) =
    extension.apply {
        properties {
            property("sonar.host.url", providers.gradleProperty("sonar.host.url").get())
            property("sonar.organization", providers.gradleProperty("sonar.organization").get())
            property(
                "sonar.projectKey",
                "${providers.gradleProperty("sonar.organization").get()}_${project.name}",
            )
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                providers.gradleProperty("sonar.coverage.jacoco.xml.report.paths").get(),
            )
            property("sonar.androidLint.reportPaths", providers.gradleProperty("sonar.android.lint.report.paths").get())
        }
    }
