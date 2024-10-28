plugins {
    id(
        projectLibs.plugins.convention.root
            .get().pluginId,
    )
}

private val Project.projectLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("projectLibs")

////plugins {
//    // this is necessary to avoid the plugins to be loaded multiple times
//    // in each subproject's classloader
//    alias(libs.plugins.androidApplication) apply false
//    alias(libs.plugins.androidLibrary) apply false
//    alias(libs.plugins.composeMultiplatform) apply false
//    alias(libs.plugins.composeCompiler) apply false
//    alias(libs.plugins.kotlinJvm) apply false
//    alias(libs.plugins.kotlinMultiplatform) apply false
//}