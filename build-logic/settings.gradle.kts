@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

rootProject.name = "build-logic"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

buildscript {
    dependencies {
//        implementation("org.tomlj:tomlj:1.1.1")
    }
}
