package plugin.cmp

import com.android.build.gradle.BaseExtension
import extension.bundle
import extension.config.configComposeAndroidBaseExtension
import extension.id
import extension.lib
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import plugin.kmp.KMPPlugin
import plugin.cmp.extension.config.configComposeExtension

internal class CMPPlugin(
    private val androidPluginId: String,
) : Plugin<Project> {
    override fun apply(target: Project): Unit =
        KMPPlugin(androidPluginId).apply(target).also {
            with(target) {
                with(pluginManager) {
                    apply(id("compose.multiplatform"))
                    apply(id("compose.compiler"))
                }

                extensions.configure<BaseExtension>(::configComposeAndroidBaseExtension)

                extensions.configure<ComposeExtension>(::configComposeExtension)

                val composeDeps = extensions.getByType<ComposeExtension>().dependencies

                extensions.configure<KotlinMultiplatformExtension> {
                    sourceSets.apply {
                        commonMain.dependencies {
                            implementation(composeDeps.runtime)
                            implementation(composeDeps.foundation)
//                                implementation(composeDeps.material)
                            implementation(composeDeps.material3)
                            implementation(composeDeps.ui)
                            implementation(composeDeps.components.resources)
                            implementation(composeDeps.components.uiToolingPreview)
                            implementation(composeDeps.material3AdaptiveNavigationSuite)
                            implementation(composeDeps.materialIconsExtended)
//                            implementation(bundle("compose.icons"))
//                            implementation(bundle("androidx.lifecycle"))
//                            implementation(lib("navigation.compose"))
//                            implementation(lib("material.navigation"))
//                            implementation(bundle("material3.adaptive"))
//                            implementation(bundle("compose.settings.ui"))
//                            implementation(lib("filekit.compose"))
//                            implementation(bundle("koin.compose.multiplatform"))
                        }

                        commonTest.dependencies {
                            implementation(lib("koin.test"))
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
                        }

                        wasmJsMain.dependencies {
                            implementation(composeDeps.html.core)
                        }
                    }
                }
            }
        }
}
