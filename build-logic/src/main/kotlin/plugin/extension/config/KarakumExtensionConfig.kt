package plugin.extension.config

import io.github.sgrishchenko.karakum.gradle.plugin.KarakumExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import plugin.extension.settings
import java.io.File

internal fun Project.configureKarakumExtension(extension: KarakumExtension): KarakumExtension =
    extension.apply {
        with(settings.extension) {
            configFile.set(File(karakumConfFile))
        }
    }
