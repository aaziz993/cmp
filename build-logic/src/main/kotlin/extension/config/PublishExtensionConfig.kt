package extension.config

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import extension.settings

internal fun Project.configPublishExtension(extension: MavenPublishBaseExtension) =
    extension.apply {
        settings.config.applyTo("publish", this)
    }
