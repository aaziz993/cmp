package plugin.multiplatform.extension.config.kmp

import org.gradle.api.Project
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import plugin.extension.bundle
import plugin.extension.compose
import plugin.extension.lib

internal fun Project.configureComposeKotlinMultiplatformExtension(extension: KotlinMultiplatformExtension) = extension.apply {
    sourceSets.apply {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation(lib("compose.colorpicker"))
            implementation(bundle("compose.icons"))
            implementation(bundle("coil.compose"))
//                            implementation(lib("squircle.shape"))
            implementation(bundle("material3.adaptive"))
            implementation(bundle("compose.settings.ui"))
            implementation(bundle("androidx.paging"))
            implementation(bundle("androidx.lifecycle"))
            implementation(bundle("androidx.navigation"))
            implementation(lib("filekit.compose"))
            implementation(bundle("koin.compose.multiplatform"))
//            implementation(lib("circuit.foundation"))
        }

        commonTest.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        getByName("mobileMain").dependencies {
            implementation(lib("permissions.compose"))
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(lib("androidx.activity.compose"))
        }

        iosMain.dependencies {
            implementation(lib("androidx.paging.runtime.uikit"))
        }

        jsMain.dependencies {
            implementation(compose.html.core)
        }
    }
}
