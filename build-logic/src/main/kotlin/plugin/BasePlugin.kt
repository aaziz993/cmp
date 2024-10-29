package plugin

import com.apollographql.apollo3.gradle.api.ApolloExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension
import org.sonarqube.gradle.SonarExtension
import extension.config.configKspExtension
import extension.config.configNoArgExtension
import extension.config.configAllOpenExtension
import extension.config.configSpotlessExtension
import extension.config.configSonarExtension
import extension.config.configDokka
import extension.config.configApolloExtension
import extension.config.configPowerAssertGradleExtension
import extension.config.configKotlinCompilationTask
import extension.config.configPublishExtension
import extension.id
import org.apache.groovy.parser.antlr4.GroovyParser.PowerExprAltContext
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.powerassert.gradle.PowerAssertGradleExtension

public class BasePlugin : Plugin<Project> {
        override fun apply(target: Project): Unit = with(target) {
                with(pluginManager) {
                        apply(id("spotless"))
                        apply(id("sonarqube"))
                        apply(id("dokka"))
                        apply(id("build.config"))
                        apply(id("ksp"))
                        apply(id("noarg"))
                        apply(id("allopen"))
                        apply(id("kotlin.serialization"))
                        apply(id("sqldelight"))
                        apply(id("room"))
                        apply(id("dataframe"))
                        apply(id("apollo3"))
                        apply(id("power.assert"))
                        apply(id("kotest.multiplatform"))
                }

                // Compiler processor for generating code during compilation
                extensions.configure<KspExtension>(::configKspExtension)

                // Generate no arg contructor by specified annotation
                extensions.configure<NoArgExtension>(::configNoArgExtension)

                // Make class open for inheritance by specified annotation
                extensions.configure<AllOpenExtension>(::configAllOpenExtension)

                // Code format check and fix
                extensions.configure<SpotlessExtension>(::configSpotlessExtension)

                // Code quality check
                extensions.configure<SonarExtension>(::configSonarExtension)

                // Create project documentation
                configDokka()

                // Providing detailed failure messages with contextual information during testing.
                @OptIn(ExperimentalKotlinGradlePluginApi::class)
                extensions.configure<PowerAssertGradleExtension>(::configPowerAssertGradleExtension)

                // GraphQL
                extensions.configure<ApolloExtension>(::configApolloExtension)

                // Configure kotlin compilation task
                tasks.withType<KotlinCompilationTask<*>>().configureEach { configKotlinCompilationTask(this) }

                tasks.getByName("clean") {
                        doLast {
                                delete(rootProject.layout.buildDirectory)
                        }
                }
        }
}