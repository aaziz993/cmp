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
import plugin.extension.config.androidTestImplementation
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

                val composeDeps = extensions.getByType<ComposeExtension>().dependencies

                extensions.configure<KotlinMultiplatformExtension> {
                    sourceSets.apply {
                        commonMain.dependencies {
                            implementation(composeDeps.runtime)
                            implementation(composeDeps.foundation)
                            implementation(composeDeps.material3)
                            implementation(composeDeps.ui)
                            implementation(composeDeps.components.resources)
                            implementation(composeDeps.components.uiToolingPreview)
                            implementation(composeDeps.material3AdaptiveNavigationSuite)
                            implementation(composeDeps.materialIconsExtended)
                            implementation(lib("compose.colorpicker"))
                            implementation(bundle("compose.icons"))
                            implementation(bundle("material3.adaptive"))
                            implementation(bundle("compose.settings.ui"))
                            implementation(bundle("androidx.multiplatform"))
                            implementation(lib("filekit.compose"))
                            implementation(bundle("koin.compose.multiplatform"))
                        }

                        commonTest.dependencies {
                            @OptIn(ExperimentalComposeLibrary::class)
                            implementation(composeDeps.uiTest)
                        }

                        jvmMain {
                            dependencies {
                                implementation(composeDeps.desktop.currentOs)
                            }
                        }

                        getByName("mobileMain").dependencies {
                            implementation(lib("permissions.compose"))
                        }

                        androidMain.dependencies {
                            implementation(composeDeps.preview)
                            implementation(lib("androidx.activity.compose"))
                        }

                        jsMain.dependencies {
                            implementation(composeDeps.html.core)
                        }
                    }
                }

                dependencies.apply {
                    debugImplementation(composeDeps.uiTooling)
                    androidTestImplementation(lib("androidx.uitest.junit4"))
                    debugImplementation(lib("androidx.uitest.test.manifest"))
                }
            }
        }
}
