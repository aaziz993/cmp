plugins {
    id(projectLibs.plugins.kmp.lib.convention.get().pluginId)
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