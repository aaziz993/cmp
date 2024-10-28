package plugin.extension

import VERSION_CATALOG_NAME
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.GradleInternal
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named(VERSION_CATALOG_NAME)

internal fun Project.id(alias: String): String = libs.findPlugin(alias).get().get().pluginId

internal val Project.settings: Settings
    get() = (gradle as GradleInternal).settings