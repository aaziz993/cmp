package plugin.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create

public class SettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings): Unit =
        with(settings) {
            extensions.create<SettingsPluginExtension>(SettingsPluginExtension.NAME, settings)
        }
}



