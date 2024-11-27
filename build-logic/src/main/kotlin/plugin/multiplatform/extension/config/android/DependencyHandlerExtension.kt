@file:Suppress("UnstableApiUsage")

package plugin.multiplatform.extension.config.android

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

public fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)

public fun DependencyHandler.coreLibraryDesugaring(dependencyNotation: Any): Dependency? =
    add("coreLibraryDesugaring", dependencyNotation)



