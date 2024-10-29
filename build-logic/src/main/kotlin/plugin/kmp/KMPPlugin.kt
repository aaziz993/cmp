package plugin.kmp

import app.cash.sqldelight.gradle.SqlDelightExtension
import com.android.build.gradle.BaseExtension
import com.google.devtools.ksp.gradle.KspExtension
import extension.config.configAndroidBaseExtension
import extension.config.configKotlinProjectExtension
import extension.config.configSqlDelightExtension
import extension.config.kspCommonMainMetadata
import extension.id
import extension.lib
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import plugin.BasePlugin
import plugin.kmp.extension.config.configKMPExtension

internal class KMPPlugin(
    private val androidPluginId: String,
) : Plugin<Project> {
    override fun apply(target: Project): Unit = BasePlugin().apply(target).also {
        with(target) {
            with(pluginManager) {
                apply(id("kotlin.multiplatform"))
                apply(androidPluginId)
                apply(id("sqldelight"))
                apply(id("room"))
            }

            extensions.configure<KotlinProjectExtension>(::configKotlinProjectExtension)

            extensions.configure<KotlinMultiplatformExtension>(::configKMPExtension)

            extensions.configure<BaseExtension>(::configAndroidBaseExtension)

            extensions.configure<SqlDelightExtension>(::configSqlDelightExtension)

            dependencies.apply {
                kspCommonMainMetadata(lib("arrow.optics.ksp.plugin"))
                kspCommonMainMetadata(lib("room.compiler"))
                kspCommonMainMetadata(lib("ktorfit.ksp"))
                // Use import org.koin.ksp.generated.*
                kspCommonMainMetadata(lib("koin.ksp.compiler"))
            }

            // In older Kotlin/KSP versions, it was necessary to manually add the KSP output as a source directory with kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin"). That’s no longer required as it’s automatically configured now. Same goes for the dependsOn kspCommonMainKotlinMetadata workaround you might find online. That’s why it’s recommended to use the latest dependencies.
            tasks.withType<KotlinCompilationTask<*>>().configureEach {
                if (name != "kspCommonMainKotlinMetadata") {
                    dependsOn("kspCommonMainKotlinMetadata")
                }
            }

            extensions.configure<KspExtension> {
                // 0 - Turn off all Ktorfit related error checking, 1 - Check for errors, 2 - Turn errors into warnings
                arg("Ktorfit_Errors", "1")
                // Compile Safety - check your Koin config at compile time (since 1.3.0)
                arg("KOIN_CONFIG_CHECK", "true")
                arg("KOIN_DEFAULT_MODULE", "false")
                // to generate viewModel Koin definition with org.koin.compose.viewmodel.dsl.viewModel instead of regular org.koin.androidx.viewmodel.dsl.viewModel
                arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
            }
        }
    }
}
