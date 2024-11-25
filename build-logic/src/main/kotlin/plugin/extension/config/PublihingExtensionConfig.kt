package plugin.extension.config

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.extension
import plugin.extension.settings

internal fun Project.configurePublishingExtension(extension: PublishingExtension) = extension.apply {
    with(settings.extension) {
        afterEvaluate {
            repositories {
                // SPACE PACKAGES
                maven {
                    name = "SpacePackages"

                    url = uri(providers.gradleProperty("jetbrains.space.packages.${projectVersionName}s.url").get())

                    credentials {
                        username = if (System.getenv().containsKey("JB_SPACE_${projectVersionName.uppercase()}S_USERNAME")) {
                            System.getenv("JB_SPACE_${projectVersionName.uppercase()}S_USERNAME")
                        }
                        else {
                            providers.gradleProperty("jetbrains.space.${projectVersionName}s.username").get()
                        }
                        password = if (System.getenv().containsKey("JB_SPACE_${projectVersionName.uppercase()}S_PASSWORD")) {
                            System.getenv("JB_SPACE_${projectVersionName.uppercase()}S_PASSWORD")
                        }
                        else {
                            localProperties.getProperty("jetbrains.space.${projectVersionName}s.password")
                        }
                    }
                }

                // GITHUB PACKAGES
                maven {
                    name = "GithubPackages"

                    url = uri(
                        "${
                            providers.gradleProperty("github.packages.${projectVersionName}s.url").get()
                        }/${rootProject.name}",
                    )

                    // Repository username and password
                    credentials {
                        username = githubUsername
                        password = if (System.getenv().containsKey("GITHUB_${projectVersionName.uppercase()}S_PASSWORD")) {
                            System.getenv("GITHUB_${projectVersionName.uppercase()}S_PASSWORD")
                        }
                        else {
                            localProperties.getProperty("github.${projectVersionName}s.password")
                        }
                    }
                }
            }
        }
    }
}
