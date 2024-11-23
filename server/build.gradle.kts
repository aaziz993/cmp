plugins {
    id(projectLibs.plugins.java.app.convention.get().pluginId)
}

dependencies {
    implementation(projects.kmpLib)
    implementation(projects.cmpLib)
    implementation(projects.shared)
    ksp(projects.compiler)
}
