package plugin.kmp.extension.config

import plugin.extension.bundle
import plugin.extension.lib
import plugin.extension.version
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget


internal fun Project.configKMPExtension(extension: KotlinMultiplatformExtension) =
    extension.apply {
        jvm()

        androidTarget(::configKotlinAndroidTarget)

        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { configKotlinIosTarget(it) }

        // Apply the default hierarchy again. It'll create additional source sets:
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        applyDefaultHierarchyTemplate {
            common {
                group("jvmAndAndroid") {
                    withJvm()
                    withAndroidTarget()
                }

                group("mobile") {
                    withAndroidTarget()
                    group("ios") {
                        withIos()
                    }
                }

                group("nonMobile") {
                    withJvm()
                    withWasmJs()
                    withWasmWasi()
                    withJs()
                }
            }
        }

        sourceSets.apply {
            commonMain {
                kotlin.srcDir(projectDir.resolve("build/generated/ksp/metadata/commonMain/kotlin"))

                dependencies {
                    implementation(lib("km.logging"))
                    implementation(lib("kotlinx.datetime"))
                    implementation(lib("kotlinx.uuid.core"))
                    implementation(bundle("kotlinx.serialization"))
                    implementation(bundle("serialization"))
                    implementation(bundle("coroutines"))
                    implementation(lib("atomicfu"))
                    implementation(lib("bignum"))
                    implementation(lib("kasechange"))
                    implementation(bundle("cryptography"))
                    implementation(lib("colormath"))
                    implementation(bundle("arrow"))
                    implementation(bundle("okio"))
                    implementation(bundle("multiplatform.settings"))
                    implementation(bundle("sqldelight"))
                    implementation(lib("room.runtime"))
                    implementation(bundle("ktor.serialization"))
                    implementation(lib("ktorfit.lib"))
                    implementation(bundle("apollo"))
                    implementation(bundle("ktor.client"))
                    implementation(bundle("compass"))
                    implementation(bundle("koin.kotlin.multiplatform"))
                }
            }

            commonTest.dependencies {
                implementation(bundle("kotest.multiplatform"))
                implementation(lib("kotlinx.coroutines.test"))
                implementation(lib("koin.test"))
                implementation(lib("okio.fakefilesystem"))
            }

            // Intermediate dependencies for jvm and android
            getByName("jvmAndAndroidMain").dependencies {
                implementation(lib("itext.core"))
                implementation(lib("cryptography.provider.jdk"))
                implementation(bundle("pgpainless"))
                implementation(lib("jackson.dataformat.xml"))
                implementation(lib("commons.net"))
                implementation(lib("jsch"))
                implementation(lib("sshj"))
                implementation(lib("ktor.client.okhttp"))
            }

            jvmMain.dependencies {
                implementation(lib("logback.classic"))
                implementation(lib("reflections"))
                implementation(lib("jackson.dataformat.xml"))
                implementation(lib("jackcess"))
                implementation(lib("jnativehook"))
                implementation(bundle("jna"))
                implementation(bundle("webcam.capture"))
                implementation(lib("kotlinx.coroutines.swing"))
                implementation(lib("sqldelight.sqlite.driver"))
                implementation(bundle("jdbc"))
                implementation(bundle("r2dbc"))
                implementation(bundle("kotysa"))
                implementation(lib("dataframe"))
                implementation(bundle("ktor.server"))
                implementation(bundle("kgraphql"))
                implementation(bundle("metrics"))
                implementation(lib("worldwind"))
                implementation(bundle("koin.ktor"))
            }

            jvmTest.dependencies {
                implementation(lib("kotest.runner.junit5"))
                implementation(lib("ktor.server.test.host"))
                implementation(lib("koin.test.junit5"))
            }

            getByName("mobileMain").dependencies {
                implementation(bundle("compass.mobile"))
                implementation(lib("permissions"))
            }

            getByName("mobileTest").dependencies {
                implementation(lib("permissions.test"))
            }

            androidMain.dependencies {
                implementation(lib("kotlinx.coroutines.android"))
                implementation(lib("android.documentation.plugin"))
                implementation(lib("androidx.activity.ktx"))
                implementation(lib("androidx.fragment.ktx"))
                implementation(lib("sqldelight.android.driver"))
                implementation(lib("room.runtime.android"))
                implementation(lib("permissions"))
                implementation(lib("worldwind"))
            }

            iosMain {
                dependencies {
                    implementation(lib("cryptography.provider.openssl3"))
                    implementation(lib("sqldelight.native.driver"))
                    implementation(lib("ktor.client.darwin"))
                    implementation(lib("permissions"))
                }
            }

            getByName("nonMobileMain").dependencies {
                implementation(lib("compass.geocoder.web.googlemaps"))
            }

            jsMain {
                val karakumGeneratedDir = projectDir.resolve("src/jsMain/generated")
                if (karakumGeneratedDir.exists()) {
                    kotlin.srcDir(karakumGeneratedDir)
                }
                dependencies {
                    implementation(lib("seskar"))
                    api(lib("kotlin-browser"))
                    api(lib("kotlin-node"))
                    implementation(npm("@js-joda/timezone", version("js.joda.timezone").toString()))
                    implementation(devNpm("copy-webpack-plugin", version("copy.webpack.plugin").toString()))
                    implementation(npm("encoding-japanese", version("encoding.japanese").toString()))
                    implementation(npm("@types/encoding-japanese", version("encoding.japanese.types").toString()))
                    // The synchronous sqljs-driver (pre-2.0) has been replaced with the asynchronous web-worker-driver. This requires configuring the generateAsync setting in your Gradle configuration.
                    implementation(lib("sqldelight.web.worker.driver"))
                    implementation(lib("compass.geolocation.browser"))
                    implementation(lib("ktor.client.js"))
                    implementation(lib("worldwind"))
                }
            }
        }

        // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
        targets.withType<KotlinNativeTarget> { configKotlinNativeTarget(this) }
    }
