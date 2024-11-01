plugins {
    id(projectLibs.plugins.cmp.app.convention.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.kmpLib)
            implementation(projects.cmpLib)
            implementation(projects.shared)
        }
    }
}
