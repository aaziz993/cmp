package plugin.multiplatform.kmp

import androidx.room.gradle.RoomExtension
import app.cash.sqldelight.gradle.SqlDelightExtension
import com.android.build.gradle.BaseExtension
import de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration
import plugin.extension.id
import plugin.extension.lib
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import plugin.BasePlugin
import plugin.extension.config.*
import plugin.multiplatform.extension.config.configureBaseExtension
import plugin.extension.config.configureKotlinProjectExtension
import plugin.extension.config.configureRoomExtension
import plugin.extension.config.configureSqlDelightExtension
import plugin.extension.config.kspCommonMainMetadata
import plugin.extension.config.configureKarakumExtension
import io.github.sgrishchenko.karakum.gradle.plugin.KarakumExtension
import io.github.sgrishchenko.karakum.gradle.plugin.tasks.KarakumGenerate
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import plugin.multiplatform.extension.config.configureKotlinMultiplatformExtension

internal class KMPPlugin(
    private val androidPluginId: String,
) : Plugin<Project> {

    override fun apply(target: Project): Unit = BasePlugin().apply(target).also {
        with(target) {
            with(pluginManager) {
                apply(id("js.plain.objects"))
                apply(id("karakum"))
                apply(id("kotlin.multiplatform"))
                apply(id("ktorfit"))
                apply(androidPluginId)
                apply(id("sqldelight"))
//                apply(id("room"))
            }

            extensions.configure<KotlinProjectExtension>(::configureKotlinProjectExtension)

            extensions.configure<KotlinMultiplatformExtension>(::configureKotlinMultiplatformExtension)

            // Http client generator
            extensions.configure<KtorfitGradleConfiguration>(::configureKtorfitGradle)

            // Android base extension
            extensions.configure<BaseExtension>(::configureBaseExtension)

            // Generates kotlin code from typescript
            extensions.configure<KarakumExtension>(::configureKarakumExtension)

            tasks.withType<KarakumGenerate> { configureKarakumGenerateTask(this) }

            tasks.withType<Kotlin2JsCompile>().configureEach { configureKotlin2JsCompileTask(this) }

            extensions.configure<SqlDelightExtension>(::configureSqlDelightExtension)

//            extensions.configure<RoomExtension>(::configureRoomExtension)

            dependencies.apply {
                kspCommonMainMetadata(lib("arrow.optics.ksp.plugin"))
                kspCommonMainMetadata(lib("androidx.room.compiler"))
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

            // Apply only last because of jsMainImplementation error
            apply(plugin = id("seskar"))
        }
    }
}
