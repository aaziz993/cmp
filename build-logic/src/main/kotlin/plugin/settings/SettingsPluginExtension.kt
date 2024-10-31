@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

package plugin.settings

import ALL_WARNINGS_AS_ERRORS
import KARAKUM_CONF_FILE
import PROJECT_GROUP
import PROJECT_VERSION_IS_SNAPSHOT
import VERSION_CATALOG_FILE
import VERSION_CATALOG_NAME
import com.moandjiezana.toml.Toml
import java.io.Serializable
import java.net.URI
import java.util.*
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.caching.http.HttpBuildCache
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.extension
import org.gradle.kotlin.dsl.gitHooks
import org.gradle.kotlin.dsl.maven
import org.slf4j.LoggerFactory

public open class SettingsPluginExtension(
    private val target: Settings,
) : Serializable {

    private val logger = LoggerFactory.getLogger(SettingsPluginExtension::class.java)

    private val providers = target.providers

    public val allWarningsAsErrors: Boolean =
        providers.gradleProperty("project.license.text.url").getOrElse(ALL_WARNINGS_AS_ERRORS.toString())
            .toBoolean()

    public var versionCatalogFile: String = VERSION_CATALOG_FILE

    public var karakumConfFile: String = KARAKUM_CONF_FILE

    public val developerName: String = providers.gradleProperty("project.developer.name").get()

    public val developerEmail: String = providers.gradleProperty("project.developer.email").get()

    public val os: OperatingSystem = OperatingSystem.current()

    public val localProperties: Properties = target.layout.rootDirectory.file("local.properties").asFile.let { file ->
        Properties().apply {
            if (file.exists()) {
                load(file.reader())
            }
        }
    }

    public val projectGroup: String = providers.gradleProperty("project.group").getOrElse(PROJECT_GROUP)

    public val projectVersionIsSnapshot: Boolean =
        providers.gradleProperty("project.version.snapshot").getOrElse(PROJECT_VERSION_IS_SNAPSHOT.toString())
            .toBoolean()

    public val projectVersionSuffix: String = if (projectVersionIsSnapshot) {
        "snapshots"
    }
    else {
        "releases"
    }

    public lateinit var projectVersion: String

    public val projectInceptionYear: String = providers.gradleProperty("project.inception.year").get()

    public val projectLicenseName: String = providers.gradleProperty("project.license.name").get()

    public val projectLicenseUrl: String = providers.gradleProperty("project.license.text.url").get()

    public val spaceUsername: String? =
        if (System.getenv().containsKey("JB_SPACE_${projectVersionSuffix.uppercase()}_USERNAME")) {
            System.getenv("JB_SPACE_${projectVersionSuffix.uppercase()}_USERNAME")
        }
        else {
            providers.gradleProperty("jetbrains.space.$projectVersionSuffix.username").get()
        }

    public val spacePassword: String? =
        if (System.getenv().containsKey("JB_SPACE_${projectVersionSuffix.uppercase()}_PASSWORD")) {
            System.getenv("JB_SPACE_${projectVersionSuffix.uppercase()}_PASSWORD")
        }
        else {
            localProperties.getProperty("jetbrains.space.$projectVersionSuffix.password")
        }

    public val spacePackagesUrl: String =
        providers.gradleProperty("jetbrains.space.packages.$projectVersionSuffix.url").get()

    public val githubUsername: String =
        if (System.getenv().containsKey("GITHUB_${projectVersionSuffix.uppercase()}_USERNAME")) {
            System.getenv("GITHUB_${projectVersionSuffix.uppercase()}_USERNAME")
        }
        else {
            providers.gradleProperty("github.$projectVersionSuffix.username").get()
        }

    public val githubPassword: String =
        if (System.getenv().containsKey("GITHUB_${projectVersionSuffix.uppercase()}_PASSWORD")) {
            System.getenv("GITHUB_${projectVersionSuffix.uppercase()}_PASSWORD")
        }
        else {
            localProperties.getProperty("github.$projectVersionSuffix.password")
        }

    public val githubPackagesUrl: String =
        "${
            providers.gradleProperty("github.packages.$projectVersionSuffix.url").get()
        }/${target.rootProject.name}"

    public fun apply(): Unit = with(target) {
        with(extension) {
            enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

            with(pluginManager) {
                apply("org.gradle.toolchains.foojay-resolver-convention")
                apply("org.danilopianini.gradle-pre-commit-git-hooks")
            }

            dependencyResolutionManagement {
                repositories {
                    google()
                    mavenCentral()
                    // Jetbrains Development
                    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
                    // Sonatype OSS Snapshot Repository
                    maven("https://oss.sonatype.org/content/repositories/snapshots")
                    // Space Packages releases
                    maven("https://maven.pkg.jetbrains.space/aaziz93/p/aaziz-93/releases")
                    // Space Packages snapshots
                    maven("https://maven.pkg.jetbrains.space/aaziz93/p/aaziz-93/snapshots")
                    // GitHub Packages
                    maven("https://maven.pkg.github.com/aaziz993")
                }
                versionCatalogs {
                    create(VERSION_CATALOG_NAME) {
                        from(layout.rootDirectory.files(versionCatalogFile))
                    }
                }
            }

            gitHooks {

            }

            buildCache {
                if (providers.gradleProperty("jetbrains.space.gradle.build.enable").get().toBoolean()) {
                    remote<HttpBuildCache>(HttpBuildCache::class.java) {
                        url = URI(
                            "${
                                providers.gradleProperty("jetbrains.space.gradle.build.cache.url").get()
                            }/${rootProject.name}",
                        )
                        // better make it a variable and set it to true only for CI builds
                        isPush = true
                        credentials {
                            username = if (System.getenv().containsKey("JB_SPACE_GRADLE_BUILD_CACHE_USERNAME")) {
                                System.getenv("JB_SPACE_GRADLE_BUILD_CACHE_USERNAME")
                            }
                            else {
                                localProperties.getProperty("jetbrains.space.gradle.build.cache.username")
                            }
                            password = if (System.getenv().containsKey("")) {
                                System.getenv("JB_SPACE_GRADLE_BUILD_CACHE_PASSWORD")
                            }
                            else {
                                localProperties.getProperty("jetbrains.space.gradle.build.cache.password")
                            }
                        }
                    }
                }
            }
        }

        projectVersion = calculateProjectVersion()

        logger.info("Applied settings plugin extension")
    }

    private fun calculateProjectVersion() = Toml().read(target.layout.rootDirectory.file("build-logic/gradle/test.toml").asFile).let {
        "${
            it.getString("project-version-major")
        }.${
            it.getString("project-version-minor")
        }.${
            it.getString("project-version-patch")
        }${
            if (providers.gradleProperty(
                    "github.actions.versioning.ref.name",
                ).get().toBoolean() &&
                System.getenv().containsKey("GITHUB_REF_NAME")
            ) {
                // The GITHUB_REF_NAME provide the reference name.
                "-${System.getenv("GITHUB_REF_NAME")}"
            }
            else {
                ""
            }
        }${
            if (providers.gradleProperty(
                    "github.actions.versioning.run.number",
                ).get().toBoolean() &&
                System.getenv().containsKey("GITHUB_RUN_NUMBER")
            ) {
                // The GITHUB_RUN_NUMBER A unique number for each run of a particular workflow in a repository.
                // This number begins at 1 for the workflow's first run, and increments with each new run.
                // This number does not change if you re-run the workflow run.
                "-${System.getenv("GITHUB_RUN_NUMBER")}"
            }
            else {
                ""
            }
        }${
            if (providers.gradleProperty(
                    "jetbrains.space.automation.versioning.ref.name",
                ).get().toBoolean() &&
                System.getenv().containsKey("JB_SPACE_GIT_BRANCH")
            ) {
                // The JB_SPACE_GIT_BRANCH provide the reference  as "refs/heads/repository_name".
                "-${System.getenv("JB_SPACE_GIT_BRANCH").substringAfterLast("/")}"
            }
            else {
                ""
            }
        }${
            if (providers.gradleProperty(
                    "jetbrains.space.automation.versioning.run.number",
                ).get().toBoolean() &&
                System.getenv().containsKey("JB_SPACE_EXECUTION_NUMBER")
            ) {
                "-${System.getenv("JB_SPACE_EXECUTION_NUMBER")}"
            }
            else {
                ""
            }
        }${
            providers.gradleProperty("project.version.suffix").get().let {
                if (it.isEmpty()) {
                    ""
                }
                else {
                    "-$it"
                }
            }
        }${
            if (projectVersionIsSnapshot) "-SNAPSHOT" else ""
        }"
    }

    public companion object {

        internal const val NAME = "SettingsPluginExtension"
    }
}
