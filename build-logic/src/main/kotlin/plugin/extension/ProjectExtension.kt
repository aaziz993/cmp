package plugin.extension

import VERSION_CATALOG_NAME
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.GradleInternal
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named(VERSION_CATALOG_NAME)

internal fun Project.id(alias: String): String = libs.findPlugin(alias).get().get().pluginId

internal fun Project.version(alias: String): VersionConstraint = libs.findVersion(alias).get()

internal fun Project.lib(alias: String): Provider<MinimalExternalModuleDependency> = libs.findLibrary(alias).get()

internal fun Project.bundle(alias: String): Provider<ExternalModuleDependencyBundle> = libs.findBundle(alias).get()

internal val Project.settings: Settings
    get() = (gradle as GradleInternal).settings