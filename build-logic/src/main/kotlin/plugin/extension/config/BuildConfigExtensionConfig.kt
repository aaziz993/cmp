package plugin.extension.config

import com.github.gmazzo.gradle.plugins.BuildConfigExtension
import org.gradle.api.Project

internal fun Project.configureBuildConfigExtension(extension: BuildConfigExtension): BuildConfigExtension =
    extension.apply {

    }
