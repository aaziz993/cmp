package plugin

import com.android.build.gradle.LibraryExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import extension.config.configComposeAndroidLibExtension
import extension.config.configPublishExtension
import extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

internal class BaseLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(id("vanniktech.maven.publish"))
        }

        // Andoird base library extension
        extensions.configure<LibraryExtension>(::configComposeAndroidLibExtension)

        extensions.configure<KotlinMultiplatformExtension> {
            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
                browser {
                    webpackTask {
                        mainOutputFileName.set(path.split(":").drop(1).joinToString("-"))
                    }
                    commonWebpackConfig {
                        cssSupport {
                            enabled.set(true)
                        }
                    }
                }
                binaries.executable()
            }
        }

        // Publishing
        extensions.configure<MavenPublishBaseExtension>(::configPublishExtension)
    }
}