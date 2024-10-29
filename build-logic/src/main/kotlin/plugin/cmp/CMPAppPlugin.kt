package plugin.cmp

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import extension.config.configComposeAndroidBaseAppExtension
import extension.config.configKtorfitGradle
import extension.config.kspCommonMainMetadata
import de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration
import extension.id
import extension.lib
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import plugin.cmp.extension.config.configComposeAppExtension

public class CMPAppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        CMPPlugin(
            target.id("android.application")
        ).apply(target).also {
            with(target) {
                extensions.configure<KotlinMultiplatformExtension> {
                    sourceSets.apply {
                        commonMain.dependencies {

                        }

                        androidMain.dependencies {
                            implementation(lib("androidx.activity.compose"))
                        }
                    }
                }

                extensions.configure<BaseAppModuleExtension>(::configComposeAndroidBaseAppExtension)

                extensions.configure<ComposeExtension>(::configComposeAppExtension)
            }
        }
}
