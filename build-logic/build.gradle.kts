import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-gradle-plugin`
    // Support convention plugins written in Kotlin.
    // Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

group = "ai.tech"
version = "1.0.0"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    explicitApi()
}

// Having a plugin in plugins { ... } block with apply false has only one use. That is to add that plugin to the build script classpath. So the according action with a convention plugin would be to declare those plugins as runtimeOnly` dependencies for that convention plugin build. The plugin itself would then not do any actions but will just be applied so that its dependencies are dragged into the classpath too.
dependencies {
    // Gradle
    // plugin
    compileOnly(libs.kotlin.gradle.plugin)
    //  provides a repository for downloading JVMs
    implementation(libs.plugins.foojay.resolver.convention.toDep())
    // build config
    runtimeOnly(libs.plugins.build.config.toDep())
    // pre-commit hooks
    implementation(libs.plugins.gradle.pre.commit.git.hooks.toDep())
    // publishing
    implementation(libs.plugins.vanniktech.maven.publish.toDep())
//    runtimeOnly(libs.plugins.vanniktech.maven.publish.toDep())

    // Kotlin
    runtimeOnly(libs.plugins.ksp.toDep())
    // generate no arg contructor by specified annotation
    implementation(libs.plugins.noarg.toDep())
//    runtimeOnly(libs.plugins.noarg.toDep())
    // make class open for inheritance by specified annotation
    implementation(libs.plugins.allopen.toDep())
//    runtimeOnly(libs.plugins.allopen.toDep())
    // serialization
    runtimeOnly(libs.plugins.kotlin.serialization.toDep())
    // multiplatform
    implementation(libs.plugins.kotlin.multiplatform.toDep())
    // compiler processor for generating code during compilation
    implementation(libs.plugins.ksp.toDep())
    // generate coverage report
    implementation(libs.plugins.kover.toDep())
//    runtimeOnly(libs.plugins.kover.toDep())
    // code format check and fix
    implementation(libs.plugins.spotless.toDep())
//    runtimeOnly(libs.plugins.spotless.toDep())
    // code analysis
    implementation(libs.plugins.sonarqube.toDep())
//    runtimeOnly(libs.plugins.sonarqube.toDep())
    // documentation
    implementation(libs.plugins.dokka.toDep())
//    runtimeOnly(libs.plugins.dokka.toDep())
    // provides a repository for downloading JVMs
    implementation(libs.plugins.binary.compatibility.validator.toDep())
    // the tool that produces Kotlin source example files and tests from markdown documents with embedded snippets of Kotlin code
    implementation(libs.plugins.knit.toDep())
    // jvm
    runtimeOnly(libs.plugins.kotlin.jvm.toDep())
    // data pipleline processing
    runtimeOnly(libs.plugins.dataframe.toDep())
    // providing detailed failure messages with contextual information during testing.
    implementation(libs.plugins.power.assert.toDep())
//    runtimeOnly(libs.plugins.power.assert.toDep())
    // testing
    runtimeOnly(libs.plugins.kotest.multiplatform.toDep())

    // Compose multiplatform
    implementation(libs.plugins.compose.multiplatform.toDep())
//    runtimeOnly(libs.plugins.compose.multiplatform.toDep())
    runtimeOnly(libs.plugins.compose.compiler.toDep())

    // Android
    compileOnly(libs.android.gradle.plugin)
    runtimeOnly(libs.plugins.android.library.toDep())
    implementation(kotlin("android-extensions"))
    implementation(kotlin("script-runtime"))

    // Web
    runtimeOnly(libs.plugins.js.plain.objects.toDep())
    implementation(libs.plugins.seskar.toDep())
    implementation(libs.plugins.karakum.toDep())

    // SQLDelight
    implementation(libs.plugins.sqldelight.toDep())
//    runtimeOnly(libs.plugins.sqldelight.toDep())

    // Room
    implementation(libs.plugins.room.toDep())
//    runtimeOnly(libs.plugins.room.toDep())

    // Ktor
    runtimeOnly(libs.plugins.ktor.toDep())

    // Http client generator
    implementation(libs.plugins.ktorfit.toDep())
//    runtimeOnly(libs.plugins.ktorfit.toDep())

    // GraphQL
    implementation(libs.plugins.apollo3.toDep())
//    runtimeOnly(libs.plugins.apollo3.toDep())
}

gradlePlugin {
    plugins {
        register("SettingsPlugin") {
            id =
                libs.plugins.settings.convention
                    .get()
                    .pluginId
            implementationClass = "plugin.settings.SettingsPlugin"
        }

        register("RootPlugin") {
            id =
                libs.plugins.root.convention
                    .get()
                    .pluginId
            implementationClass = "plugin.root.RootPlugin"
        }

        register("JavaAppPlugin") {
            id =
                libs.plugins.java.app.convention
                    .get()
                    .pluginId
            implementationClass = "plugin.java.JavaAppPlugin"
        }

        register("KMPLibPlugin") {
            id =
                libs.plugins.kmp.lib.convention
                    .get()
                    .pluginId
            implementationClass = "plugin.kmp.KMPLibPlugin"
        }

        register("CMPLibPlugin") {
            id =
                libs.plugins.cmp.lib.convention
                    .get()
                    .pluginId
            implementationClass = "plugin.cmp.CMPLibPlugin"
        }
        register("CMPAppPlugin") {
            id =
                libs.plugins.cmp.app.convention
                    .get()
                    .pluginId
            implementationClass = "plugin.cmp.CMPAppPlugin"
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

fun Provider<PluginDependency>.toDep() =
    map {
        "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
    }

tasks.getByName("clean") {
    doLast {
        delete(rootProject.layout.buildDirectory)
    }
}
