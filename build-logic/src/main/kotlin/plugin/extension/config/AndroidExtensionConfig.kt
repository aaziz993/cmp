@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

package plugin.extension.config

import ANDROID_JAVA_SOURCE_VERSION
import ANDROID_JAVA_TARGET_VERSION
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import java.util.*
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.get
import plugin.extension.version

internal fun Project.configBaseExtension(
    extension: BaseExtension,
) = extension.apply {
    namespace = project.group.toString()
    compileSdkVersion = "android-${version("android.compile.sdk")}"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    val proguardFile = "proguard.pro"
    val consumerProguardFile = "consumer-proguard.pro"

    defaultConfig {
        minSdk = version("android.min.sdk").toString().toInt()

        targetSdk = version("android.target.sdk").toString().toInt()

        versionCode = version("android.version.code").toString().toInt()

        versionName = version("android.version.name").toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles(consumerProguardFile)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            debuggable(false)
            proguardFiles(
                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                // getDefaultProguardFile() is a simple helper method that fetches them out of build/intermediates/proguard-files.
                // The Android Gradle Plugin (AGP) puts them there.
                getDefaultProguardFile("proguard-android-optimize.txt"),

                // List additional ProGuard rules for the given build type here. By default,
                // Android Studio creates and includes an empty rules file for you (located
                // at the root directory of each module).

                // Includes a local, custom Proguard rules file
                "proguard-rules.pro",
            )
            testProguardFiles(
                // The proguard files listed here are included in the
                // test APK only.
                "test-proguard-rules.pro",
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            debuggable(true)
            proguardFile(proguardFile)
        }
    }

    (this as CommonExtension<*, *, *, *, *, *>).apply {
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        lint {
            checkReleaseBuilds = false
            abortOnError = false
        }
    }

    compileOptions {
        sourceCompatibility =
            providers.gradleProperty("android.compile.options.source.compatibility").getOrElse(ANDROID_JAVA_SOURCE_VERSION.toString()).toInt().toJavaVersion()
        targetCompatibility =
            providers.gradleProperty("android.compile.options.target.compatibility").getOrElse(ANDROID_JAVA_TARGET_VERSION.toString()).toInt().toJavaVersion()
    }
}

internal fun Project.configLibraryExtension(extension: LibraryExtension) = extension.apply {

}

internal fun Project.configBaseAppModuleExtension(extension: BaseAppModuleExtension) = extension.apply {
    defaultConfig {
        applicationId = group.toString()
    }

    (this as AppExtension).apply {

        buildTypes {
            getByName("release") {
                isShrinkResources = true
            }
            getByName("debug") {
                isShrinkResources = false
            }
        }
    }

    buildFeatures {
        compose = true
    }
}

public fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)



