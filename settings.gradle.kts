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
    id("convention.settings")
}

config {
    group = "ai.tech"
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

//include(":composeApp")
//include(":server")
//include(":shared")