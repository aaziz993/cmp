@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

package plugin.settings

import GROUP
import VERSION
import VERSION_CATALOG_FILE
import VERSION_CATALOG_NAME
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.config
import org.gradle.kotlin.dsl.gitHooks
import org.gradle.kotlin.dsl.maven
import org.slf4j.LoggerFactory
import java.io.File
import java.io.Serializable

public open class SettingsPluginExtension(
    private val target: Settings,
) : Serializable {
    private val logger = LoggerFactory.getLogger(SettingsPluginExtension::class.java)

    public var group: String = GROUP

    public var version: String = VERSION

    public var versionCatalogFile: String = VERSION_CATALOG_FILE

    private val applyToMap = mutableMapOf<String, Any>()

    internal fun applyTo(
        key: String,
        obj: Any,
    ) {
        applyToMap[key] = obj
        logger.info("Configured \"$key\" on \"${obj::class.qualifiedName}\"")
    }

    public fun create(): Unit = with(target) {
        with(config) {
            enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

            with(pluginManager) {
                apply("org.gradle.toolchains.foojay-resolver-convention")
                apply("org.danilopianini.gradle-pre-commit-git-hooks")
            }

            dependencyResolutionManagement {
                repositories {
                    google()
                    mavenCentral()
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
        }

        logger.info("Applied settings plugin extension")
    }

    public companion object {
        internal const val NAME = "plugin/settings"
    }
}
