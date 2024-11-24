@file:Suppress("UnstableApiUsage")

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
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.tomlj:tomlj:1.1.1")
    }
}
