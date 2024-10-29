plugins {
    id(projectLibs.plugins.java.app.convention.get().pluginId)
}

dependencies {
    implementation(projects.kmpLib)
//    implementation(projects.cmp.lib)
//    implementation(projects.shared)
}