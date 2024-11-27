package plugin.multiplatform.extension.config.android

import ANDROID_JAVA_SOURCE_VERSION
import ANDROID_JAVA_TARGET_VERSION
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import kotlin.text.get
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import plugin.extension.config.toJavaVersion
import plugin.extension.lib
import plugin.extension.version
import plugin.multiplatform.extension.config.android.model.BuildFlavor
import plugin.multiplatform.extension.config.android.model.BuildType
import plugin.multiplatform.extension.config.android.model.FlavorDimension

internal fun Project.configureBaseExtension(
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

        manifestPlaceholders["appAuthRedirectScheme"] = "empty"

        missingDimensionStrategy(
            FlavorDimension.contentType.name,
            BuildFlavor.demo.name,
        )

        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] =
                    "$projectDir/schemas"
            }
        }

        consumerProguardFiles(consumerProguardFile)
    }

    buildTypes {
        getByName(BuildType.RELEASE.applicationIdSuffix) {
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
        getByName(BuildType.DEBUG.applicationIdSuffix) {
            isMinifyEnabled = false
            debuggable(true)
            proguardFile(proguardFile)
        }
    }

    (this as CommonExtension<*, *, *, *, *, *>).apply {
//        flavorDimensions += FlavorDimension.contentType.name
//
//        productFlavors {
//            BuildFlavor.values().forEach {
//                create(it.name) {
//                    dimension = it.dimension.name
//                    if (this is ApplicationExtension && this is ApplicationProductFlavor) {
//                        if (it.applicationIdSuffix != null) {
//                            this.applicationIdSuffix = it.applicationIdSuffix
//                        }
//                    }
//                }
//            }
//        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        lint {
            checkReleaseBuilds = false
            abortOnError = false
        }

        dependencies {
            add("lintChecks", lib("compose.lint.checks"))
        }
    }

    compileOptions {
        sourceCompatibility =
            providers.gradleProperty("android.compile.options.source.compatibility").getOrElse(ANDROID_JAVA_SOURCE_VERSION.toString()).toInt().toJavaVersion()
        targetCompatibility =
            providers.gradleProperty("android.compile.options.target.compatibility").getOrElse(ANDROID_JAVA_TARGET_VERSION.toString()).toInt().toJavaVersion()

        isCoreLibraryDesugaringEnabled = true
    }

    dependencies {
        coreLibraryDesugaring(lib("android.desugar.jdk.libs"))
    }
}
