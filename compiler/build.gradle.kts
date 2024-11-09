plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.kmpLib)
    implementation(projectLibs.symbol.processing.api)
}
