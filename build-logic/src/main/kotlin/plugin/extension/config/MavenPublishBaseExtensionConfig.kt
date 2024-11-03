package plugin.extension.config

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import plugin.extension.settings

internal fun Project.configureMavenPublishBaseExtension(extension: MavenPublishBaseExtension) = extension.apply {
    with(settings.extension) {
        coordinates(group.toString(), rootProject.name, version.toString())

        pom {
            name.set(rootProject.name.uppercaseFirstChar())
            description.set(providers.gradleProperty("project.description").get())
            inceptionYear.set(projectInceptionYear)
            url.set("https://github.com/$githubUsername/${rootProject.name}")

            licenses {
                license {
                    name.set(projectLicenseName)
                    url.set(projectLicenseTextUrl)
                }
            }

            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/$githubUsername/${rootProject.name}/issues") // Change here
            }

            developers {
                developer {
                    id.set(providers.gradleProperty("project.developer.id").get())
                    name.set(developerName)
                    email.set(developerEmail)
                    providers.gradleProperty("project.developer.organization.name").orNull?.let {
                        organization.set(it)
                    }
                    providers.gradleProperty("project.developer.organization.url").orNull?.let {
                        organizationUrl.set(it)
                    }
                }
            }

            scm {
                connection.set("scm:git:git://github.com:$githubUsername/${rootProject.name}.git")
                url.set("https://github.com/$githubUsername/${rootProject.name}")
                developerConnection.set("scm:git:ssh://github.com:$githubUsername/${rootProject.name}.git")
            }
        }

        publishToMavenCentral(
            when (providers.gradleProperty("sonatype.${projectVersionSuffix}s.url").get()) {
                "https://oss.sonatype.org" -> "DEFAULT"
                "https://s01.oss.sonatype.org" -> "S01"
                else -> "CENTRAL_PORTAL"
            },
            providers.gradleProperty("sonatype.${projectVersionSuffix}s.autopush").get().toBoolean(),
        )

        // Enable GPG signing for all publications
        signAllPublications()
    }
}
