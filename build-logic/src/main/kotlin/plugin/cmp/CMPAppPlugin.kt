package plugin.cmp

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import extension.config.configComposeAndroidBaseAppExtension
import extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import plugin.cmp.extension.config.configDesktopExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

public class CMPAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        CMPPlugin(target.id("android.application")).apply(target)

        with(target) {
            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.apply {
                    commonMain.dependencies {

                    }

                    androidMain.dependencies {

                    }
                }
            }

            extensions.configure<BaseAppModuleExtension>(::configComposeAndroidBaseAppExtension)

            extensions.configure<KotlinMultiplatformExtension> {
                @OptIn(ExperimentalWasmDsl::class)
                wasmJs {
                    moduleName = "composeApp"
                    browser {
                        val rootDirPath = project.rootDir.path
                        val projectDirPath = project.projectDir.path
                        commonWebpackConfig {
                            outputFileName = "composeApp.js"
                            devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                                static = (static ?: mutableListOf()).apply {
                                    // Serve sources to debug inside browser
                                    add(rootDirPath)
                                    add(projectDirPath)
                                }
                            }
                        }
                    }
                    binaries.executable()
                }
            }

            extensions.configure<ComposeExtension> {
                extensions.configure<DesktopExtension>(::configDesktopExtension)
            }
        }
    }
}
