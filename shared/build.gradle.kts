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

//
//kotlin {
//    androidTarget {
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }
//    }
//
//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()
//
//    jvm()
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser {
//            val rootDirPath = project.rootDir.path
//            val projectDirPath = project.projectDir.path
//            commonWebpackConfig {
//                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
//                    static = (static ?: mutableListOf()).apply {
//                        // Serve sources to debug inside browser
//                        add(rootDirPath)
//                        add(projectDirPath)
//                    }
//                }
//            }
//        }
//    }
//
//    sourceSets {
//        commonMain.dependencies {
//            // put your Multiplatform dependencies here
//        }
//    }
//}
//
//android {
//    namespace = "ai.tech.shared"
//    compileSdk = libs.versions.android.compileSdk.get().toInt()
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//    defaultConfig {
//        minSdk = libs.versions.android.minSdk.get().toInt()
//    }
//}
