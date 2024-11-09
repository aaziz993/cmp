plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projectLibs.symbol.processing.api)
}
