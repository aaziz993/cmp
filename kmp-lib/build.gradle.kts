plugins {
    id(projectLibs.plugins.kmp.lib.convention.get().pluginId)
}
dependencies {
    implementation("io.github.flaxoos:ktor-server-task-scheduling-core:2.1.1")
    implementation("io.github.flaxoos:ktor-server-task-scheduling-redis:2.1.1")
    implementation("io.github.flaxoos:ktor-server-task-scheduling-mongodb:2.1.1")
    implementation("io.github.flaxoos:ktor-server-task-scheduling-jdbc:2.1.1")
}
repositories {
    maven {
        url = uri("https://packages.confluent.io/maven")
        name = "confluence"
    }
}

sqldelight {
    databases {
        //Note: Name of your Database and .sq file should be same
        create("KeyValue") {
            packageName.set("ai.tech.core.data.database.sqldelight")
            // Required by asynchronous web-worker-driver for js.
            generateAsync = true
        }
    }
    // To avoid library linking issues.
    linkSqlite = true
}
