package plugin.java

import plugin.extension.bundle
import plugin.extension.id
import plugin.extension.lib
import plugin.extension.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import plugin.BasePlugin
import plugin.extension.config.*
import plugin.extension.config.configureJavaPluginExtension
import plugin.extension.config.implementation
import plugin.extension.config.ksp
import plugin.extension.config.testImplementation
import plugin.java.extension.config.configureJavaApp

public class JavaAppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        BasePlugin().apply(target).also {
            with(target) {
                with(pluginManager) {
                    apply(id("kotlin.jvm"))
                    apply(id("ktor"))
                    apply("application")
                }

                extensions.configure<JavaPluginExtension>(::configureJavaPluginExtension)

                extensions.configure<JavaApplication>(::configureJavaApp)

                val osName = System.getProperty("os.name").lowercase()
                val tcnativeClassifier = when {
                    osName.contains("win") -> "windows-x86_64"
                    osName.contains("linux") -> "linux-x86_64"
                    osName.contains("mac") -> "osx-x86_64"
                    else -> null
                }

                dependencies.apply {
                    implementation(lib("logback.classic"))
                    implementation(lib("reflections"))
                    implementation(lib("kotlinx.datetime"))
                    implementation(lib("bignum"))
                    implementation(lib("kasechange"))
                    implementation(lib("jackson.dataformat.xml"))
                    implementation(lib("itext.core"))
                    implementation(lib("jackcess"))
                    implementation(lib("jnativehook"))
                    implementation(bundle("jna"))
                    implementation(bundle("webcam.capture"))
                    implementation(lib("commons.net"))
                    implementation(lib("sshj"))
                    implementation(lib("jsch"))
                    implementation(bundle("kotlinx.serialization"))
                    implementation(bundle("serialization"))
                    implementation(lib("kotlinx.coroutines.swing"))
                    implementation(lib("cryptography.provider.jdk"))
                    implementation(bundle("pgpainless"))
                    implementation(bundle("jdbc"))
                    implementation(bundle("r2dbc"))
                    implementation(lib("dataframe"))
                    implementation(lib("kandy"))
                    implementation(bundle("kotysa"))
                    implementation(bundle("ktor.serialization"))
                    implementation(bundle("ktor.server"))
                    implementation(bundle("ktor.client"))
                    implementation(lib("ktor.client.okhttp"))
                    implementation(bundle("kotlinx.rpc.serialization"))
                    implementation(bundle("kotlinx.rpc.server"))
                    implementation(lib("ktorfit.lib"))
                    implementation(bundle("kgraphql"))
                    implementation(bundle("metrics"))
                    implementation(bundle("koin.ktor"))
                    // KSP
                    ksp(lib("arrow.optics.ksp.plugin"))
                    ksp(lib("ktorfit.ksp"))
                    ksp(lib("koin.ksp.compiler"))
                    // Testing
                    testImplementation(bundle("kotest.multiplatform"))
                    testImplementation(lib("kotest.runner.junit5"))
                    testImplementation(lib("kotlinx.coroutines.test"))
                    testImplementation(lib("ktor.server.test.host"))
                    testImplementation(lib("koin.test.junit5"))

                    // To enable HTTP/2 in Netty, use OpenSSL bindings (tcnative netty port).
                    // The below shows how to add a native implementation (statically linked BoringSSL library, a fork of OpenSSL)
                    if (tcnativeClassifier != null) {
                        implementation("io.netty:netty-tcnative-boringssl-static:${version("tcnative")}:$tcnativeClassifier")
                    } else {
                        implementation("io.netty:netty-tcnative-boringssl-static:${version("tcnative")}")
                    }
                }

                extensions.configure<KotlinProjectExtension>(::configureKotlinProjectExtension)
            }
        }
}





