plugins {
    id(projectLibs.plugins.cmp.lib.convention.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.kmpLib)
            implementation(projects.cmpLib)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
