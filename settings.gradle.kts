@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

rootProject.name = "cmp"

pluginManagement {
    // Include common convention plugins.
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("settings.convention")
}

config {
    versionCatalogFile = "build-logic/gradle/libs.versions.toml"

    karakumConfFile = "build-logic/karakum.config.json"

    apply()
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":kmp-lib")
include(":cmp-lib")
include(":shared")
include(":server")
include(":composeApp")