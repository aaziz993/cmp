package plugin.extension.config

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

internal fun Project.configurePublishingExtension(extension: PublishingExtension) = extension.apply {
    publications.withType<MavenPublication>(MavenPublication::class.java, ::configureMavenPublication)
}
