package plugin.extension.config

import io.github.sgrishchenko.karakum.gradle.plugin.KarakumExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import plugin.extension.settings
import java.io.File

internal fun Project.configKarakumExtension(extension: KarakumExtension): KarakumExtension =
    extension.apply {
        configFile.set(File("../../build-logic/karakum.config.json"))
        settings.config.applyTo("karakum", this)
    }
