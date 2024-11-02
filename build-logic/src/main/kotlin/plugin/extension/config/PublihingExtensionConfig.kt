package plugin.extension.config

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.extension
import plugin.extension.settings

internal fun Project.configurePublishingExtension(extension: PublishingExtension) = extension.apply {
    with(settings.extension) {
        afterEvaluate {
            repositories {
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
}
