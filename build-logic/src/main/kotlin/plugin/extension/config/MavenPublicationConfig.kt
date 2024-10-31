package plugin.extension.config

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.extension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import plugin.extension.settings

@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun Project.configureMavenPublication(extension: MavenPublication) = extension.apply {
    with(settings.extension) {
        repositories.apply {
            // SPACE PACKAGES
            maven {
                name = "SpacePackages"

                url = uri(spacePackagesUrl)

                credentials {
                    username = spaceUsername
                    password = spaceUsername
                }
            }

            // GITHUB PACKAGES
            maven {
                name = "GithubPackages"

                url = uri(githubPackagesUrl)

                // Repository username and password
                credentials {
                    username = githubUsername
                    password = githubPassword
                }
            }
        }
    }
}
