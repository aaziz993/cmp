package plugin.cmp

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import plugin.extension.config.configComposeAndroidBaseAppExtension
import plugin.extension.id
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
            extensions.configure<BaseAppModuleExtension>(::configComposeAndroidBaseAppExtension)

            extensions.configure<KotlinMultiplatformExtension> {
//                js {
//                    browser {
//                        webpackTask {
//                            mainOutputFileName.set(path.split(":").drop(1).joinToString("-"))
//                        }
//                        commonWebpackConfig {
//                            cssSupport {
//                                enabled.set(true)
//                            }
//                        }
//                    }
//                    binaries.executable()
//                }
            }

            extensions.configure<ComposeExtension> {
                extensions.configure<DesktopExtension>(::configDesktopExtension)
            }
        }
    }
}
