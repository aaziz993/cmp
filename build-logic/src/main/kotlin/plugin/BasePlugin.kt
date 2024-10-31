package plugin

import com.apollographql.apollo3.gradle.api.ApolloExtension
import com.diffplug.gradle.spotless.SpotlessApply
import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.gmazzo.gradle.plugins.BuildConfigExtension
import com.google.devtools.ksp.gradle.KspExtension
import de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration
import plugin.extension.id
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension
import org.jetbrains.kotlin.powerassert.gradle.PowerAssertGradleExtension
import org.sonarqube.gradle.SonarExtension
import plugin.extension.config.*
import plugin.extension.config.configApolloExtension
import plugin.extension.config.configKotlinCompilationTask
import plugin.extension.config.configPowerAssertGradleExtension
import plugin.extension.config.configSonarExtension

internal class BasePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(id("kover"))
            apply(id("spotless"))
            apply(id("sonarqube"))
            apply(id("dokka"))
            apply(id("build.config"))
            apply(id("ksp"))
            apply(id("noarg"))
            apply(id("allopen"))
            apply(id("kotlin.serialization"))
            apply(id("kmp.nativecoroutines"))
            apply(id("dataframe"))
//            apply(id("kotlinx.rpc"))
            apply(id("ktorfit"))
            apply(id("apollo3"))
            apply(id("power.assert"))
            apply(id("kotest.multiplatform"))
        }

        // Test coverage analyze
        extensions.configure<KoverProjectExtension>(::configKoverProjectExtension)

        tasks.create<Task>("generateKoverReport", Task::class.java) {
            dependsOn("koverHtmlReport", "koverXmlReport", "test")
        }

        // Code format check and fix
        extensions.configure<SpotlessExtension>(::configSpotlessExtension)

        tasks.withType<SpotlessApply> {
            dependsOn("synchronizeRootFiles")
        }

        // Code quality check
        extensions.configure<SonarExtension>(::configSonarExtension)

        // Create project documentation
        configDokka()

        extensions.configure<BuildConfigExtension>(::configBuildConfigExtension)

        // Compiler processor for generating code during compilation
        extensions.configure<KspExtension>(::configKspExtension)

        // Generate no arg contructor by specified annotation
        extensions.configure<NoArgExtension>(::configNoArgExtension)

        // Make class open for inheritance by specified annotation
        extensions.configure<AllOpenExtension>(::configAllOpenExtension)

        // Providing detailed failure messages with contextual information during testing.
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        extensions.configure<PowerAssertGradleExtension>(::configPowerAssertGradleExtension)

        // Http client generator
        extensions.configure<KtorfitGradleConfiguration>(::configKtorfitGradle)

        // GraphQL
        extensions.configure<ApolloExtension>(::configApolloExtension)

        // Configure kotlin compilation task
        tasks.withType<KotlinCompilationTask<*>>().configureEach { configKotlinCompilationTask(this) }

        tasks.withType<Test> {
            finalizedBy("generateKoverReport")
        }
    }
}
