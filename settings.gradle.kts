@file:Suppress("UnstableApiUsage")

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
    versionCatalogPath = "build-logic/gradle/libs.versions.toml"

    karakumConfPath = "build-logic/karakum.config.json"

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

include(":compiler")
include(":kmp-lib")
include(":cmp-lib")
include(":shared")
include(":server")
include(":composeApp")
