@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

package plugin.extension.config

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import ANDROID_JAVA_SOURCE_VERSION
import ANDROID_JAVA_TARGET_VERSION
import plugin.extension.settings
import plugin.extension.version
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.config
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.gradle.api.artifacts.Dependency

import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun Project.configAndroidBaseExtension(
    extension: BaseExtension,
) = extension.apply {
    namespace = project.group.toString()
    compileSdkVersion = "android-${version("android-compile-sdk")}"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    val proguardFile = "proguard.pro"
    val consumerProguardFile = "consumer-proguard.pro"

    defaultConfig {
        minSdk = version("android-min-sdk").toString().toInt()

        targetSdk = version("android-target-sdk").toString().toInt()

        versionCode = version("android-version-code").toString().toInt()

        versionName = version("android-version-name").toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles(consumerProguardFile)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            debuggable(false)
            proguardFile(proguardFile)
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
        sourceCompatibility = ANDROID_JAVA_SOURCE_VERSION.toJavaVersion()
        targetCompatibility = ANDROID_JAVA_TARGET_VERSION.toJavaVersion()
    }

    settings.config.applyTo("android", this)
}

internal fun Project.configComposeAndroidBaseExtension(extension: BaseExtension) = extension.apply {

    settings.config.applyTo("android", this)
}

internal fun Project.configComposeAndroidLibExtension(extension: LibraryExtension) = extension.apply {
    settings.config.applyTo("android", this)
}

internal fun Project.configComposeAndroidBaseAppExtension(extension: BaseAppModuleExtension) = extension.apply {
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

    settings.config.applyTo("android", this)
}

public fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)



