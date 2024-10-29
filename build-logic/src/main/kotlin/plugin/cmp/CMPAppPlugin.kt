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
import plugin.cmp.extension.config.configDesktopExtension

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

            extensions.configure<ComposeExtension> {
                extensions.configure<DesktopExtension>(::configDesktopExtension)
            }
        }
    }
}
