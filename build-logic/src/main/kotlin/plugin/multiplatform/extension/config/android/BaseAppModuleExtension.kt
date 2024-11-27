package plugin.multiplatform.extension.config.android

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import plugin.multiplatform.extension.config.android.model.BuildType

internal fun Project.configureBaseAppModuleExtension(extension: BaseAppModuleExtension) = extension.apply {
    defaultConfig {
        applicationId = group.toString()
    }

    (this as AppExtension).apply {

        buildTypes {
            getByName(BuildType.RELEASE.applicationIdSuffix) {
                isShrinkResources = true
            }
            getByName(BuildType.DEBUG.applicationIdSuffix) {
                isShrinkResources = false
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}
