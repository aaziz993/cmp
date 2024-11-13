package plugin.cmp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import plugin.cmp.extension.config.configureComposeExtension
import plugin.extension.bundle
import plugin.extension.composeDeps
import plugin.extension.config.androidTestImplementation
import plugin.extension.config.configureComposeKotlinMultiplatformExtension
import plugin.extension.config.debugImplementation
import plugin.extension.id
import plugin.extension.lib
import plugin.kmp.KMPPlugin

internal class CMPPlugin(
    private val androidPluginId: String,
) : Plugin<Project> {

    @OptIn(ExperimentalComposeLibrary::class)
    override fun apply(target: Project): Unit =
        KMPPlugin(androidPluginId).apply(target).also {
            with(target) {
                with(pluginManager) {
                    apply(id("compose.multiplatform"))
                    apply(id("compose.compiler"))
                }

                extensions.configure<ComposeExtension>(::configureComposeExtension)

                extensions.configure<KotlinMultiplatformExtension>(::configureComposeKotlinMultiplatformExtension)

                dependencies.apply {
                    debugImplementation(composeDeps.uiTooling)
                    androidTestImplementation(lib("androidx.uitest.junit4"))
                    debugImplementation(lib("androidx.uitest.test.manifest"))
                }
            }
        }
}
